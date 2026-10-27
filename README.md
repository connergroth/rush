# Rush

Team Polymorphs (Conner Groth, Shaan Chauhan), CSCI 4448 semester project.

Rush is a dinner rush in a restaurant kitchen, simulated in the terminal. Orders come in on a tick clock, each ticket's items go to a station (grill, fry, saute, cold), and an expediter policy picks which ticket fires next. The idea is to run the same rush under different policies and see which one actually does best on average ticket time, tables served, and how long finished food sits at the pass waiting on the rest of the table.

Java 25, Gradle, Gson for the JSON scenario, JUnit 5 + Cucumber for tests, logback for output.

## Run

```
./gradlew run --args="shortest"
./gradlew test
```

The policy argument is `fifo` (default), `shortest` or `coursesync`.

## Patterns

- **Strategy:** `IExpoPolicy` picks the next ticket off the rail. `FifoPolicy`, `ShortestPrepPolicy` and `CourseSyncPolicy` all sit behind that one method, and `Kitchen.setPolicy` can swap them mid-run. Same idea as `IStrategy` and changing strategies in Polymorphia.
- **Observer:** `KitchenEventBus` is the subject, and `TextViewObserver` and `MetricsObserver` subscribe to it, either for everything (`ALL`) or for specific event types. It's basically our HW8 `EventBus` minus the Singleton, so the bus gets passed in instead of grabbed globally.
- **Builder:** `Kitchen.getNewBuilder()` sets up the clock, policy, event bus, stations and tickets, then `build()` checks nothing's missing and that every item has a station that can cook it. Modeled on `Maze.Builder`.
- **Factory:** `TicketFactory` takes the `Menu`, looks up item names, numbers the tickets in order, and builds them from the JSON scenario. Same setup as `ArtifactFactory`.
- **Template Method:** `Station.tick` is final and fixes the order every station follows (cook, finish, start the next items). Each station subclass just fills in `capacity()` and `cookTime()`, so the grill runs 4 things at once and the saute station slows down when it's crowded.

## Dependency injection

`Kitchen` gets its stations, `Clock`, `IExpoPolicy` and `IKitchenSubject` through its constructor (through `Kitchen.Builder`) and doesn't create any of them itself. Stations get the event bus the same way, and `TextViewObserver` gets its `PrintStream`. `Rush.main` is the only place any of it gets created, which is what lets the tests swap in `FixedClock` and `RecordingObserver`.

## Persisted state

The menu and the rush scenario load from JSON in `src/main/resources/scenarios/`. Writing each run's events out to a log file is planned for the final.
