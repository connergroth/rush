package rush.tickets;

import org.junit.jupiter.api.Test;
import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.menu.StationType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TicketFactoryTest {
    private final Menu menu = new Menu(List.of(
            new MenuItem("Burger", StationType.GRILL, 6, 2),
            new MenuItem("Fries", StationType.FRY, 3, 1)));

    @Test
    void looksItemsUpInTheMenuAndNumbersTicketsInOrder() {
        TicketFactory factory = new TicketFactory(menu);

        Ticket first = factory.createTicket(7, 0, List.of("burger", "Fries"));
        Ticket second = factory.createTicket(3, 2, List.of("Fries"));

        assertEquals(1, first.id());
        assertEquals(2, second.id());
        assertEquals(StationType.GRILL, first.items().get(0).station());
        assertEquals(first, first.items().get(1).ticket());
    }

    @Test
    void unknownItemIsRejected() {
        TicketFactory factory = new TicketFactory(menu);
        assertThrows(IllegalArgumentException.class, () -> factory.createTicket(7, 0, List.of("Lobster")));
    }
}
