package rush.observers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KitchenEventBusTest {
    private KitchenEventBus bus;

    @BeforeEach
    void setUp() {
        bus = new KitchenEventBus();
    }

    @Test
    void observersGetTheEventsTheyAttachedFor() {
        RecordingObserver firedOnly = new RecordingObserver();
        RecordingObserver everything = new RecordingObserver();
        bus.attach(firedOnly, List.of(EventType.TICKET_FIRED));
        bus.attach(everything);

        bus.postEvent(EventType.TICKET_ARRIVED, "[t=0] #1 (table 4)");
        bus.postEvent(EventType.TICKET_FIRED, "[t=0] #1 (table 4)");

        assertEquals(List.of(new RecordingObserver.Event(EventType.TICKET_FIRED, "[t=0] #1 (table 4)")),
                firedOnly.events());
        assertEquals(2, everything.events().size());
    }

    @Test
    void observerAttachedTwiceIsNotifiedOnce() {
        RecordingObserver observer = new RecordingObserver();
        bus.attach(observer);
        bus.attach(observer, List.of(EventType.TICKET_FIRED));

        bus.postEvent(EventType.TICKET_FIRED, "[t=3] #2 (table 7)");

        assertEquals(1, observer.count(EventType.TICKET_FIRED));
    }

    @Test
    void detachedObserverReceivesNothing() {
        RecordingObserver observer = new RecordingObserver();
        bus.attach(observer);
        bus.attach(observer, List.of(EventType.TICKET_SERVED));
        bus.detach(observer);

        bus.postEvent(EventType.TICKET_SERVED, "[t=5] #1 (table 4)");
        bus.postEvent(EventType.TICK_COMPLETED, "[t=5]");

        assertTrue(observer.events().isEmpty());
    }
}
