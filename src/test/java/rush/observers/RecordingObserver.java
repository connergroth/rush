package rush.observers;

import java.util.ArrayList;
import java.util.List;

public class RecordingObserver implements IKitchenObserver {
    public record Event(EventType type, String message) {
    }

    private final List<Event> events = new ArrayList<>();

    @Override
    public void update(EventType type, String message) {
        events.add(new Event(type, message));
    }

    public List<Event> events() {
        return List.copyOf(events);
    }

    public long count(EventType type) {
        return events.stream().filter(event -> event.type() == type).count();
    }
}
