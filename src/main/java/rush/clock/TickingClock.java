package rush.clock;

public class TickingClock implements Clock {
    private int tick = 0;

    @Override
    public int currentTick() {
        return tick;
    }

    @Override
    public void advance() {
        tick++;
    }
}
