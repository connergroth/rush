package rush.tickets;

import rush.menu.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ticket {
    private final int id;
    private final int table;
    private final int arrivalTick;
    private final List<OrderItem> items = new ArrayList<>();
    private int firedTick = -1;
    private int servedTick = -1;

    public Ticket(int id, int table, int arrivalTick, List<MenuItem> menuItems) {
        if (menuItems.isEmpty()) {
            throw new IllegalArgumentException("Ticket #" + id + " has no items");
        }
        this.id = id;
        this.table = table;
        this.arrivalTick = arrivalTick;
        for (MenuItem menuItem : menuItems) {
            items.add(new OrderItem(menuItem, this));
        }
    }

    public int id() {
        return id;
    }

    public int table() {
        return table;
    }

    public int arrivalTick() {
        return arrivalTick;
    }

    public List<OrderItem> items() {
        return Collections.unmodifiableList(items);
    }

    public int firedTick() {
        return firedTick;
    }

    public int servedTick() {
        return servedTick;
    }

    public boolean isFired() {
        return firedTick >= 0;
    }

    public void markFired(int tick) {
        firedTick = tick;
    }

    public boolean isComplete() {
        return items.stream().allMatch(OrderItem::isDone);
    }

    public void markServed(int tick) {
        if (!isComplete()) {
            throw new IllegalStateException("Ticket #" + id + " can't be served before every item is done");
        }
        servedTick = tick;
    }

    public boolean isServed() {
        return servedTick >= 0;
    }

    public int longestPrep() {
        return items.stream().mapToInt(item -> item.menuItem().prepTicks()).max().orElse(0);
    }

    public int ticketTime() {
        return servedTick - arrivalTick;
    }

    // How long the first finished plate sat at the pass waiting for the rest of the table.
    public int waitAtPass() {
        int firstDone = items.stream().mapToInt(OrderItem::completedTick).min().orElse(servedTick);
        return servedTick - firstDone;
    }

    @Override
    public String toString() {
        return "#" + id + " (table " + table + ")";
    }
}
