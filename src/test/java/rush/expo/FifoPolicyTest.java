package rush.expo;

import org.junit.jupiter.api.Test;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.tickets.Ticket;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FifoPolicyTest {
    private static final MenuItem BURGER = new MenuItem("Burger", StationType.GRILL, 6, 2);
    private final IExpoPolicy policy = new FifoPolicy();

    @Test
    void picksTheEarliestArrival() {
        Ticket late = new Ticket(1, 10, 5, List.of(BURGER));
        Ticket early = new Ticket(2, 11, 1, List.of(BURGER));
        Ticket middle = new Ticket(3, 12, 3, List.of(BURGER));

        Optional<Ticket> chosen = policy.chooseNextTicket(List.of(late, early, middle));

        assertEquals(early, chosen.orElseThrow());
    }

    @Test
    void sameArrivalGoesToTheLowerId() {
        Ticket second = new Ticket(2, 10, 0, List.of(BURGER));
        Ticket first = new Ticket(1, 11, 0, List.of(BURGER));

        assertEquals(first, policy.chooseNextTicket(List.of(second, first)).orElseThrow());
    }

    @Test
    void emptyRailChoosesNothing() {
        assertTrue(policy.chooseNextTicket(List.of()).isEmpty());
    }
}
