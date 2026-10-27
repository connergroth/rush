package rush.expo;

import rush.tickets.Ticket;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FifoPolicy implements IExpoPolicy {
    @Override
    public Optional<Ticket> chooseNextTicket(List<Ticket> rail) {
        return rail.stream()
                .min(Comparator.comparingInt(Ticket::arrivalTick)
                        .thenComparingInt(Ticket::id));
    }
}
