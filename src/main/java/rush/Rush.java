package rush;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rush.clock.TickingClock;
import rush.expo.CourseSyncPolicy;
import rush.expo.FifoPolicy;
import rush.expo.IExpoPolicy;
import rush.expo.ShortestPrepPolicy;
import rush.kitchen.Kitchen;
import rush.observers.KitchenEventBus;
import rush.observers.MetricsObserver;
import rush.observers.TextViewObserver;
import rush.scenario.Scenario;
import rush.scenario.ScenarioLoader;
import rush.stations.ColdStation;
import rush.stations.FryStation;
import rush.stations.GrillStation;
import rush.stations.SauteStation;
import rush.tickets.TicketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;

// Composition root: the only place concrete clocks, policies, buses, observers and stations get created.
public class Rush {
    private static final Logger logger = LoggerFactory.getLogger(Rush.class);
    private static final String SCENARIO = "/scenarios/friday-rush.json";
    private static final int MAX_TICKS = 500;
    private static final Map<String, Supplier<IExpoPolicy>> POLICIES = Map.of(
            "fifo", FifoPolicy::new,
            "shortest", ShortestPrepPolicy::new,
            "coursesync", CourseSyncPolicy::new);

    public static void main(String[] args) throws IOException {
        String policyName = args.length > 0 ? args[0].toLowerCase() : "fifo";
        Supplier<IExpoPolicy> policy = POLICIES.get(policyName);
        if (policy == null) {
            throw new IllegalArgumentException("Unknown policy " + policyName + ", pick one of " + POLICIES.keySet());
        }

        Scenario scenario = loadScenario();
        TicketFactory ticketFactory = new TicketFactory(scenario.toMenu());

        KitchenEventBus bus = new KitchenEventBus();
        MetricsObserver metricsObserver = new MetricsObserver();
        bus.attach(new TextViewObserver(System.out));
        bus.attach(metricsObserver);

        Kitchen kitchen = Kitchen.getNewBuilder()
                .withClock(new TickingClock())
                .withPolicy(policy.get())
                .withEventBus(bus)
                .addStation(new GrillStation(bus))
                .addStation(new FryStation(bus))
                .addStation(new SauteStation(bus))
                .addStation(new ColdStation(bus))
                .addTickets(ticketFactory.createTickets(scenario))
                .build();

        kitchen.runUntilDone(MAX_TICKS);

        logger.info("Policy: {}", policyName);
        logger.info(metricsObserver.report());
        logger.info(kitchen.metrics().toString());
    }

    private static Scenario loadScenario() throws IOException {
        InputStream stream = Rush.class.getResourceAsStream(SCENARIO);
        if (stream == null) {
            throw new IOException("Missing scenario resource " + SCENARIO);
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return new ScenarioLoader().load(reader);
        }
    }
}
