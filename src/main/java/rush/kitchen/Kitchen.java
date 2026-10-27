package rush.kitchen;

import rush.clock.Clock;
import rush.expo.IExpoPolicy;
import rush.menu.StationType;
import rush.observers.EventType;
import rush.observers.IKitchenSubject;
import rush.stations.Station;
import rush.tickets.OrderItem;
import rush.tickets.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Kitchen {
    private final Clock clock;
    private IExpoPolicy policy;
    private final IKitchenSubject bus;
    private final Map<StationType, Station> stations;
    private final List<Ticket> tickets;
    private final List<Ticket> incoming;
    private final List<Ticket> rail = new ArrayList<>();
    private final List<Ticket> fired = new ArrayList<>();

    Kitchen(Clock clock, IExpoPolicy policy, IKitchenSubject bus,
            Map<StationType, Station> stations, List<Ticket> incoming) {
        this.clock = clock;
        this.policy = policy;
        this.bus = bus;
        this.stations = Collections.unmodifiableMap(new EnumMap<>(stations));
        this.tickets = incoming.stream()
                .sorted(Comparator.comparingInt(Ticket::arrivalTick).thenComparingInt(Ticket::id))
                .toList();
        this.incoming = new ArrayList<>(tickets);
    }

    public static Builder getNewBuilder() {
        return new Builder();
    }

    public void tick() {
        int now = clock.currentTick();
        takeArrivals(now);
        fireNextTicket(now);
        for (Station station : stations.values()) {
            station.tick(now);
        }
        serveCompleteTickets(now);
        bus.postEvent(EventType.TICK_COMPLETED, "[t=" + now + "]");
        clock.advance();
    }

    public void runUntilDone(int maxTicks) {
        for (int ticks = 0; ticks < maxTicks && !allServed(); ticks++) {
            tick();
        }
    }

    public void setPolicy(IExpoPolicy policy) {
        this.policy = policy;
    }

    public List<Ticket> tickets() {
        return tickets;
    }

    public boolean allServed() {
        return tickets.stream().allMatch(Ticket::isServed);
    }

    public RunMetrics metrics() {
        List<Ticket> served = tickets.stream().filter(Ticket::isServed).toList();
        int tablesServed = (int) served.stream().mapToInt(Ticket::table).distinct().count();
        double averageTicketTime = served.stream().mapToInt(Ticket::ticketTime).average().orElse(0);
        double averageWait = served.stream().mapToInt(Ticket::waitAtPass).average().orElse(0);
        int maxWait = served.stream().mapToInt(Ticket::waitAtPass).max().orElse(0);
        return new RunMetrics(tablesServed, averageTicketTime, averageWait, maxWait);
    }

    private void takeArrivals(int now) {
        while (!incoming.isEmpty() && incoming.getFirst().arrivalTick() <= now) {
            Ticket ticket = incoming.removeFirst();
            rail.add(ticket);
            bus.postEvent(EventType.TICKET_ARRIVED, "[t=" + now + "] " + ticket);
        }
    }

    // One fire per tick: the expediter is one person.
    private void fireNextTicket(int now) {
        Optional<Ticket> next = policy.chooseNextTicket(Collections.unmodifiableList(rail));
        if (next.isEmpty()) {
            return;
        }
        Ticket ticket = next.get();
        ticket.markFired(now);
        for (OrderItem item : ticket.items()) {
            stations.get(item.station()).enqueue(item);
        }
        rail.remove(ticket);
        fired.add(ticket);
        bus.postEvent(EventType.TICKET_FIRED, "[t=" + now + "] " + ticket);
    }

    private void serveCompleteTickets(int now) {
        for (Ticket ticket : fired) {
            if (ticket.isComplete() && !ticket.isServed()) {
                ticket.markServed(now);
                bus.postEvent(EventType.TICKET_SERVED, "[t=" + now + "] " + ticket
                        + " in " + ticket.ticketTime() + " ticks");
            }
        }
    }

    public static class Builder {
        private Clock clock;
        private IExpoPolicy policy;
        private IKitchenSubject bus;
        private final Map<StationType, Station> stations = new EnumMap<>(StationType.class);
        private final List<Ticket> tickets = new ArrayList<>();

        private Builder() {
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder withPolicy(IExpoPolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder withEventBus(IKitchenSubject bus) {
            this.bus = bus;
            return this;
        }

        public Builder addStation(Station station) {
            stations.put(station.type(), station);
            return this;
        }

        public Builder addTickets(List<Ticket> tickets) {
            this.tickets.addAll(tickets);
            return this;
        }

        public Kitchen build() {
            require(clock != null, "a clock");
            require(policy != null, "an expo policy");
            require(bus != null, "an event bus");
            require(!stations.isEmpty(), "at least one station");
            for (Ticket ticket : tickets) {
                for (OrderItem item : ticket.items()) {
                    require(stations.containsKey(item.station()),
                            "a " + item.station() + " station for " + item.menuItem().name() + " on " + ticket);
                }
            }
            return new Kitchen(clock, policy, bus, stations, tickets);
        }

        private static void require(boolean present, String missing) {
            if (!present) {
                throw new IllegalStateException("Kitchen needs " + missing);
            }
        }
    }
}
