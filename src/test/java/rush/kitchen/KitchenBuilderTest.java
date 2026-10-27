package rush.kitchen;

import org.junit.jupiter.api.Test;
import rush.clock.FixedClock;
import rush.expo.FifoPolicy;
import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.observers.KitchenEventBus;
import rush.stations.GrillStation;
import rush.tickets.TicketFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KitchenBuilderTest {
    private final KitchenEventBus bus = new KitchenEventBus();
    private final TicketFactory ticketFactory = new TicketFactory(new Menu(List.of(
            new MenuItem("Burger", StationType.GRILL, 6, 2),
            new MenuItem("Scampi", StationType.SAUTE, 6, 2))));

    @Test
    void buildsWhenEveryPieceIsThere() {
        Kitchen kitchen = Kitchen.getNewBuilder()
                .withClock(new FixedClock(0))
                .withPolicy(new FifoPolicy())
                .withEventBus(bus)
                .addStation(new GrillStation(bus))
                .addTickets(List.of(ticketFactory.createTicket(1, 0, List.of("Burger"))))
                .build();
        assertNotNull(kitchen);
    }

    @Test
    void missingPolicyIsRejected() {
        Kitchen.Builder builder = Kitchen.getNewBuilder()
                .withClock(new FixedClock(0))
                .withEventBus(bus)
                .addStation(new GrillStation(bus));

        IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(error.getMessage().contains("policy"));
    }

    @Test
    void ticketItemWithNoStationIsRejected() {
        Kitchen.Builder builder = Kitchen.getNewBuilder()
                .withClock(new FixedClock(0))
                .withPolicy(new FifoPolicy())
                .withEventBus(bus)
                .addStation(new GrillStation(bus))
                .addTickets(List.of(ticketFactory.createTicket(1, 0, List.of("Burger", "Scampi"))));

        IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(error.getMessage().contains("SAUTE"));
    }
}
