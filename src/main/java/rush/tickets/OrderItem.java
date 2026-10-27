package rush.tickets;

import rush.menu.MenuItem;
import rush.menu.StationType;

public class OrderItem {
    private final MenuItem menuItem;
    private final Ticket ticket;
    private boolean done = false;
    private int completedTick = -1;

    OrderItem(MenuItem menuItem, Ticket ticket) {
        this.menuItem = menuItem;
        this.ticket = ticket;
    }

    public MenuItem menuItem() {
        return menuItem;
    }

    public Ticket ticket() {
        return ticket;
    }

    public StationType station() {
        return menuItem.station();
    }

    public boolean isDone() {
        return done;
    }

    public int completedTick() {
        return completedTick;
    }

    public void markDone(int tick) {
        done = true;
        completedTick = tick;
    }

    @Override
    public String toString() {
        return menuItem.name() + " for #" + ticket.id();
    }
}
