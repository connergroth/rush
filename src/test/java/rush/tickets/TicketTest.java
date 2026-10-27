package rush.tickets;

import org.junit.jupiter.api.Test;
import rush.menu.MenuItem;
import rush.menu.StationType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketTest {
    private static final MenuItem SALAD = new MenuItem("Salad", StationType.COLD, 2, 1);
    private static final MenuItem SALMON = new MenuItem("Salmon", StationType.GRILL, 5, 2);

    @Test
    void notCompleteUntilEveryItemIsDone() {
        Ticket ticket = new Ticket(1, 4, 1, List.of(SALAD, SALMON));
        assertFalse(ticket.isComplete());

        ticket.items().get(0).markDone(3);
        assertFalse(ticket.isComplete());
        assertThrows(IllegalStateException.class, () -> ticket.markServed(3));

        ticket.items().get(1).markDone(6);
        assertTrue(ticket.isComplete());
    }

    @Test
    void ticketTimeAndWaitAtPassAfterServing() {
        Ticket ticket = new Ticket(1, 4, 1, List.of(SALAD, SALMON));
        ticket.markFired(1);
        ticket.items().get(0).markDone(3);
        ticket.items().get(1).markDone(6);

        ticket.markServed(6);

        assertTrue(ticket.isServed());
        assertEquals(5, ticket.ticketTime());
        // the salad sat at the pass from tick 3 until the salmon caught up at tick 6
        assertEquals(3, ticket.waitAtPass());
        assertEquals(5, ticket.longestPrep());
    }

    @Test
    void itemsCannotBeChangedFromOutside() {
        Ticket ticket = new Ticket(1, 4, 0, List.of(SALAD));
        assertThrows(UnsupportedOperationException.class, () -> ticket.items().clear());
    }
}
