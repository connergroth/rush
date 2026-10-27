package rush.stations;

import rush.menu.StationType;
import rush.observers.IKitchenSubject;
import rush.tickets.OrderItem;

public class FryStation extends Station {
    public FryStation(IKitchenSubject bus) {
        super(StationType.FRY, bus);
    }

    @Override
    protected int capacity() {
        return 2;
    }

    // Fryers run on a timer, so a full basket never slows anything down.
    @Override
    protected int cookTime(OrderItem item) {
        return item.menuItem().prepTicks();
    }
}
