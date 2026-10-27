package rush.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import rush.clock.FixedClock;
import rush.expo.FifoPolicy;
import rush.expo.IExpoPolicy;
import rush.expo.ShortestPrepPolicy;
import rush.kitchen.Kitchen;
import rush.menu.Menu;
import rush.menu.MenuItem;
import rush.menu.StationType;
import rush.observers.KitchenEventBus;
import rush.stations.ColdStation;
import rush.stations.FryStation;
import rush.stations.GrillStation;
import rush.stations.SauteStation;
import rush.tickets.Ticket;
import rush.tickets.TicketFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KitchenStepDefs {
    private static final Map<String, Supplier<IExpoPolicy>> POLICIES = Map.of(
            "fifo", FifoPolicy::new,
            "shortest", ShortestPrepPolicy::new);

    private final TicketFactory ticketFactory = new TicketFactory(new Menu(List.of(
            new MenuItem("Steak", StationType.GRILL, 5, 2),
            new MenuItem("Fries", StationType.FRY, 3, 1),
            new MenuItem("Scampi", StationType.SAUTE, 6, 2),
            new MenuItem("Salad", StationType.COLD, 2, 1))));
    private final KitchenEventBus bus = new KitchenEventBus();
    private final List<Ticket> tickets = new ArrayList<>();
    private IExpoPolicy policy;
    private Kitchen kitchen;

    @Given("a kitchen using the {word} policy")
    public void aKitchenUsingThePolicy(String policyName) {
        policy = POLICIES.get(policyName).get();
    }

    @Given("these tickets:")
    public void theseTickets(List<Map<String, String>> rows) {
        for (Map<String, String> row : rows) {
            List<String> items = Arrays.stream(row.get("items").split(",")).map(String::trim).toList();
            tickets.add(ticketFactory.createTicket(
                    Integer.parseInt(row.get("table")), Integer.parseInt(row.get("arrival")), items));
        }
    }

    @When("the kitchen runs until done")
    public void theKitchenRunsUntilDone() {
        kitchen = Kitchen.getNewBuilder()
                .withClock(new FixedClock(0))
                .withPolicy(policy)
                .withEventBus(bus)
                .addStation(new GrillStation(bus))
                .addStation(new FryStation(bus))
                .addStation(new SauteStation(bus))
                .addStation(new ColdStation(bus))
                .addTickets(tickets)
                .build();
        kitchen.runUntilDone(100);
    }

    @Then("ticket {int} is fired first")
    public void ticketIsFiredFirst(int id) {
        Ticket first = tickets.stream().min(Comparator.comparingInt(Ticket::firedTick)).orElseThrow();
        assertEquals(id, first.id());
    }

    @Then("every ticket is served")
    public void everyTicketIsServed() {
        assertTrue(kitchen.allServed());
    }
}
