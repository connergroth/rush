package rush.clock;

public class FixedClock implements Clock {
    private int tick;

    public FixedClock(int tick) {
        this.tick = tick;
    }

    @Override
    public int currentTick() {
        return tick;
    }

    @Override
    public void advance() {
        tick++;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    @Override
    public String toString() {
        return "FixedClock{" + tick + '}';
    }
}
