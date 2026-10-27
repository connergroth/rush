package rush.menu;

// course 1 = appetizer, 2 = entree
public record MenuItem(String name, StationType station, int prepTicks, int course) {
    public MenuItem {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Menu item needs a name");
        }
        if (station == null) {
            throw new IllegalArgumentException("Menu item " + name + " needs a station");
        }
        if (prepTicks <= 0) {
            throw new IllegalArgumentException("Menu item " + name + " needs prepTicks > 0, got " + prepTicks);
        }
    }
}
