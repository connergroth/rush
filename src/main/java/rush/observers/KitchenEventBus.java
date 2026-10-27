package rush.observers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitchenEventBus implements IKitchenSubject {
    private final Map<EventType, List<IKitchenObserver>> observers = new HashMap<>();

    @Override
    public void attach(IKitchenObserver observer) {
        observers.putIfAbsent(EventType.ALL, new ArrayList<>());
        observers.get(EventType.ALL).add(observer);
    }

    @Override
    public void attach(IKitchenObserver observer, List<EventType> events) {
        for (EventType event : events) {
            observers.putIfAbsent(event, new ArrayList<>());
            observers.get(event).add(observer);
        }
    }

    @Override
    public void detach(IKitchenObserver observer) {
        for (List<IKitchenObserver> observers : observers.values()) {
            observers.remove(observer);
        }
    }

    @Override
    public void postEvent(EventType type, String message) {
        Set<IKitchenObserver> observersToNotify = new HashSet<>();
        observersToNotify.addAll(observers.getOrDefault(type, new ArrayList<>()));
        observersToNotify.addAll(observers.getOrDefault(EventType.ALL, new ArrayList<>()));

        for (IKitchenObserver observer : observersToNotify) {
            observer.update(type, message);
        }
    }
}
