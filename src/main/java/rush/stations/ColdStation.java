package rush.stations;

import rush.menu.StationType;
import rush.observers.IKitchenSubject;

public class ColdStation extends Station {
    public ColdStation(IKitchenSubject bus) {
        super(StationType.COLD, bus);
    }

    @Override
    protected int capacity() {
        return 2;
    }
}
