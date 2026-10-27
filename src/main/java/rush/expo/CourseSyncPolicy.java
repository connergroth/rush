package rush.expo;

import rush.tickets.Ticket;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;

// Fires the ticket whose items finish closest together, so less food waits at the pass.
public class CourseSyncPolicy implements IExpoPolicy {
    @Override
    public Optional<Ticket> chooseNextTicket(List<Ticket> rail) {
        return rail.stream()
                .min(Comparator.comparingInt(CourseSyncPolicy::prepSpread)
                        .thenComparingInt(Ticket::arrivalTick)
                        .thenComparingInt(Ticket::id));
    }

    private static int prepSpread(Ticket ticket) {
        IntSummaryStatistics prep = ticket.items().stream()
                .mapToInt(item -> item.menuItem().prepTicks())
                .summaryStatistics();
        return prep.getMax() - prep.getMin();
    }
}
