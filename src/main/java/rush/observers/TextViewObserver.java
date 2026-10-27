package rush.observers;

import java.io.PrintStream;

public class TextViewObserver implements IKitchenObserver {
    private final PrintStream out;

    public TextViewObserver(PrintStream out) {
        this.out = out;
    }

    @Override
    public void update(EventType type, String message) {
        out.println(type + " " + message);
    }
}
