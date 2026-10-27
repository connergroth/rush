package rush.observers;

import java.util.List;

public interface IKitchenSubject {
    void attach(IKitchenObserver observer);

    void attach(IKitchenObserver observer, List<EventType> events);

    void detach(IKitchenObserver observer);

    void postEvent(EventType type, String message);
}
