package rush.expo;

import org.junit.jupiter.api.Test;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.tickets.Ticket;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortestPrepPolicyTest {
    private static final MenuItem RIBEYE = new MenuItem("Ribeye", StationType.GRILL, 9, 2);
    private static final MenuItem FRIES = new MenuItem("Fries", StationType.FRY, 3, 1);
    private static final MenuItem SALAD = new MenuItem("Salad", StationType.COLD, 2, 1);
    private final IExpoPolicy policy = new ShortestPrepPolicy();

    @Test
    void picksTheTicketWithTheShortestLongestItem() {
        Ticket steakDinner = new Ticket(1, 10, 0, List.of(RIBEYE, SALAD));
        Ticket fryBasket = new Ticket(2, 11, 1, List.of(FRIES, SALAD));
        Ticket salad = new Ticket(3, 12, 2, List.of(SALAD));

        assertEquals(salad, policy.chooseNextTicket(List.of(steakDinner, fryBasket, salad)).orElseThrow());
    }

    @Test
    void tieGoesToTheEarlierArrival() {
        Ticket later = new Ticket(1, 10, 4, List.of(FRIES));
        Ticket earlier = new Ticket(2, 11, 2, List.of(FRIES, SALAD));

        assertEquals(earlier, policy.chooseNextTicket(List.of(later, earlier)).orElseThrow());
    }

    @Test
    void emptyRailChoosesNothing() {
        assertTrue(policy.chooseNextTicket(List.of()).isEmpty());
    }
}
