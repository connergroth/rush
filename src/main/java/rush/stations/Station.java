package rush.stations;

import rush.menu.StationType;
import rush.observers.EventType;
import rush.observers.IKitchenSubject;
import rush.tickets.OrderItem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public abstract class Station {
    private final StationType type;
    private final IKitchenSubject bus;
    private final Deque<OrderItem> queue = new ArrayDeque<>();
    private final List<InProgress> active = new ArrayList<>();

    protected Station(StationType type, IKitchenSubject bus) {
        this.type = type;
        this.bus = bus;
    }

    public final List<OrderItem> tick(int now) {
        List<OrderItem> completed = new ArrayList<>();
        Iterator<InProgress> cooking = active.iterator();
        while (cooking.hasNext()) {
            InProgress inProgress = cooking.next();
            inProgress.remaining--;
            if (inProgress.remaining <= 0) {
                cooking.remove();
                inProgress.item.markDone(now);
                completed.add(inProgress.item);
                bus.postEvent(EventType.ITEM_COMPLETED,
                        "[t=" + now + "] " + inProgress.item + " at " + type);
            }
        }
        while (active.size() < capacity() && !queue.isEmpty()) {
            OrderItem next = queue.poll();
            active.add(new InProgress(next, cookTime(next)));
        }
        return completed;
    }

    public void enqueue(OrderItem item) {
        queue.add(item);
    }

    public int load() {
        return queue.size() + active.size();
    }

    public StationType type() {
        return type;
    }

    protected int activeCount() {
        return active.size();
    }

    protected abstract int capacity();

    protected int cookTime(OrderItem item) {
        return item.menuItem().prepTicks();
    }

    private static class InProgress {
        private final OrderItem item;
        private int remaining;

        InProgress(OrderItem item, int remaining) {
            this.item = item;
            this.remaining = remaining;
        }
    }
}
