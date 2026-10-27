package rush.stations;

import org.junit.jupiter.api.Test;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.observers.EventType;
import rush.observers.KitchenEventBus;
import rush.observers.RecordingObserver;
import rush.tickets.OrderItem;
import rush.tickets.Ticket;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StationTest {
    private final KitchenEventBus bus = new KitchenEventBus();

    private static List<OrderItem> itemsOf(MenuItem menuItem, int count) {
        return new Ticket(1, 1, 0, Collections.nCopies(count, menuItem)).items();
    }

    @Test
    void grillNeverRunsMoreThanFourItems() {
        GrillStation grill = new GrillStation(bus);
        itemsOf(new MenuItem("Burger", StationType.GRILL, 3, 2), 6).forEach(grill::enqueue);

        for (int tick = 0; tick < 10; tick++) {
            grill.tick(tick);
            assertTrue(grill.activeCount() <= 4, "grill had " + grill.activeCount() + " items at t=" + tick);
        }
        assertEquals(0, grill.load());
    }

    @Test
    void threeTickItemCompletesThreeTicksAfterItStarts() {
        RecordingObserver observer = new RecordingObserver();
        bus.attach(observer, List.of(EventType.ITEM_COMPLETED));
        GrillStation grill = new GrillStation(bus);
        OrderItem burger = itemsOf(new MenuItem("Burger", StationType.GRILL, 3, 2), 1).getFirst();
        grill.enqueue(burger);

        assertTrue(grill.tick(0).isEmpty());
        assertTrue(grill.tick(1).isEmpty());
        assertTrue(grill.tick(2).isEmpty());
        assertEquals(List.of(burger), grill.tick(3));
        assertEquals(3, burger.completedTick());
        assertEquals(1, observer.count(EventType.ITEM_COMPLETED));
    }

    @Test
    void crowdedSautePanTakesATickLonger() {
        SauteStation saute = new SauteStation(bus);
        List<OrderItem> scampi = itemsOf(new MenuItem("Scampi", StationType.SAUTE, 2, 2), 3);
        scampi.forEach(saute::enqueue);

        saute.tick(0);
        assertTrue(saute.tick(1).isEmpty());
        // the first two started on a quiet stove, the third went in next to two pans
        assertEquals(scampi.subList(0, 2), saute.tick(2));
        assertEquals(List.of(scampi.get(2)), saute.tick(3));
    }
}
