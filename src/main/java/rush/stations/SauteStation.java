package rush.stations;

import rush.menu.StationType;
import rush.observers.IKitchenSubject;
import rush.tickets.OrderItem;

public class SauteStation extends Station {
    private static final int CROWDED_AT = 2;

    public SauteStation(IKitchenSubject bus) {
        super(StationType.SAUTE, bus);
    }

    @Override
    protected int capacity() {
        return 3;
    }

    // Crowded pans lose heat, so an item started next to 2+ others takes a tick longer.
    @Override
    protected int cookTime(OrderItem item) {
        int prep = item.menuItem().prepTicks();
        return activeCount() >= CROWDED_AT ? prep + 1 : prep;
    }
}
