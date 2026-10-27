package rush.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Menu {
    private final Map<String, MenuItem> items = new LinkedHashMap<>();

    public Menu(List<MenuItem> menuItems) {
        for (MenuItem item : menuItems) {
            items.put(key(item.name()), item);
        }
    }

    public Optional<MenuItem> find(String name) {
        return Optional.ofNullable(items.get(key(name)));
    }

    public List<MenuItem> items() {
        return List.copyOf(items.values());
    }

    private static String key(String name) {
        return name.trim().toLowerCase();
    }
}
