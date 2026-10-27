package rush.scenario;

import com.google.gson.Gson;

import java.io.Reader;

public class ScenarioLoader {
    private final Gson gson = new Gson();

    public Scenario load(Reader reader) {
        Scenario scenario = gson.fromJson(reader, Scenario.class);
        if (scenario == null || scenario.menu() == null || scenario.orders() == null) {
            throw new IllegalArgumentException("Scenario needs a menu and a list of orders");
        }
        return scenario;
    }
}
