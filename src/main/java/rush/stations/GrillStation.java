package rush.stations;

import rush.menu.StationType;
import rush.observers.IKitchenSubject;

public class GrillStation extends Station {
    public GrillStation(IKitchenSubject bus) {
        super(StationType.GRILL, bus);
    }

    @Override
    protected int capacity() {
        return 4;
    }
}
