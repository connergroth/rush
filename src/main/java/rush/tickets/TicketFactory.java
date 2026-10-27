package rush.tickets;

import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.scenario.Scenario;

import java.util.List;

public class TicketFactory {
    private final Menu menu;
    private int nextId = 1;

    public TicketFactory(Menu menu) {
        this.menu = menu;
    }

    public Ticket createTicket(int table, int arrivalTick, List<String> itemNames) {
        List<MenuItem> menuItems = itemNames.stream()
                .map(name -> menu.find(name)
                        .orElseThrow(() -> new IllegalArgumentException("No menu item named " + name)))
                .toList();
        return new Ticket(nextId++, table, arrivalTick, menuItems);
    }

    public List<Ticket> createTickets(Scenario scenario) {
        return scenario.orders().stream()
                .map(order -> createTicket(order.table(), order.arrivalTick(), order.items()))
                .toList();
    }
}
