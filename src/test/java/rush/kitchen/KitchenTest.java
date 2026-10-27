package rush.kitchen;

import org.junit.jupiter.api.Test;
import rush.clock.FixedClock;
import rush.expo.FifoPolicy;
import rush.expo.IExpoPolicy;
import rush.expo.ShortestPrepPolicy;
import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.observers.EventType;
import rush.observers.KitchenEventBus;
import rush.observers.RecordingObserver;
import rush.stations.ColdStation;
import rush.stations.GrillStation;
import rush.tickets.Ticket;
import rush.tickets.TicketFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KitchenTest {
    private static final Menu MENU = new Menu(List.of(
            new MenuItem("Steak", StationType.GRILL, 5, 2),
            new MenuItem("Salad", StationType.COLD, 2, 1)));

    private final KitchenEventBus bus = new KitchenEventBus();
    private final RecordingObserver observer = new RecordingObserver();

    // Ticket #1 (a steak) and ticket #2 (a salad) both arrive at tick 0.
    private Kitchen twoTicketKitchen(IExpoPolicy policy) {
        TicketFactory ticketFactory = new TicketFactory(MENU);
        bus.attach(observer);
        return Kitchen.getNewBuilder()
                .withClock(new FixedClock(0))
                .withPolicy(policy)
                .withEventBus(bus)
                .addStation(new GrillStation(bus))
                .addStation(new ColdStation(bus))
                .addTickets(List.of(
                        ticketFactory.createTicket(1, 0, List.of("Steak")),
                        ticketFactory.createTicket(2, 0, List.of("Salad"))))
                .build();
    }

    private static Ticket ticket(Kitchen kitchen, int id) {
        return kitchen.tickets().stream().filter(ticket -> ticket.id() == id).findFirst().orElseThrow();
    }

    @Test
    void fifoServesBothTicketsEndToEnd() {
        Kitchen kitchen = twoTicketKitchen(new FifoPolicy());

        kitchen.runUntilDone(20);

        assertTrue(kitchen.allServed());
        assertEquals(0, ticket(kitchen, 1).firedTick());
        assertEquals(1, ticket(kitchen, 2).firedTick());
        assertEquals(5, ticket(kitchen, 1).servedTick());
        assertEquals(3, ticket(kitchen, 2).servedTick());
        assertEquals(2, observer.count(EventType.TICKET_SERVED));
        assertEquals(new RunMetrics(2, 4.0, 0.0, 0), kitchen.metrics());
    }

    @Test
    void shortestPrepFiresTheSaladFirst() {
        Kitchen kitchen = twoTicketKitchen(new ShortestPrepPolicy());

        kitchen.runUntilDone(20);

        assertEquals(0, ticket(kitchen, 2).firedTick());
        assertEquals(1, ticket(kitchen, 1).firedTick());
        assertEquals(2, ticket(kitchen, 2).servedTick());
        assertEquals(6, ticket(kitchen, 1).servedTick());
    }

    @Test
    void setPolicySwapsTheStrategyMidRun() {
        Kitchen kitchen = twoTicketKitchen(new FifoPolicy());
        kitchen.setPolicy(new ShortestPrepPolicy());
        kitchen.tick();
        kitchen.setPolicy(new FifoPolicy());

        kitchen.runUntilDone(20);

        assertEquals(0, ticket(kitchen, 2).firedTick());
        assertTrue(kitchen.allServed());
    }
}
