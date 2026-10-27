package rush.scenario;

import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.menu.StationType;

import java.util.List;

public record Scenario(List<MenuItemJson> menu, List<OrderJson> orders) {

    public record MenuItemJson(String name, StationType station, int prepTicks, int course) {
        public MenuItem toMenuItem() {
            return new MenuItem(name, station, prepTicks, course);
        }
    }

    public record OrderJson(int table, int arrivalTick, List<String> items) {
    }

    public Menu toMenu() {
        return new Menu(menu.stream().map(MenuItemJson::toMenuItem).toList());
    }
}
