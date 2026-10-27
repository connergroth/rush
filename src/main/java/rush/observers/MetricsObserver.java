package rush.observers;

import java.util.EnumMap;
import java.util.Map;

public class MetricsObserver implements IKitchenObserver {
    private final Map<EventType, Integer> counts = new EnumMap<>(EventType.class);

    @Override
    public void update(EventType type, String message) {
        counts.merge(type, 1, Integer::sum);
    }

    public int count(EventType type) {
        return counts.getOrDefault(type, 0);
    }

    public String report() {
        return "Over " + count(EventType.TICK_COMPLETED) + " ticks the kitchen took "
                + count(EventType.TICKET_ARRIVED) + " tickets, fired "
                + count(EventType.TICKET_FIRED) + ", cooked "
                + count(EventType.ITEM_COMPLETED) + " items and served "
                + count(EventType.TICKET_SERVED) + " tickets.";
    }
}
