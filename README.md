# Rush

Team Polymorphs (Conner Groth, Shaan Chauhan), CSCI 4448 semester project.

A headless dinner-rush kitchen sim. Orders arrive on a seeded clock, each ticket's items route to a station (grill, fry, saute, cold), and an expediter policy decides which ticket fires next. Same scenario + same seed under different policies, compare average ticket time, tables served, and how long food sat at the pass.

Java 25, Gradle, Gson for the JSON scenario, JUnit 5 + Cucumber for tests, logback for output.

## Run

```
./gradlew run
./gradlew test
```

## Patterns

- **Strategy:** `expo/IExpoPolicy`
- **Observer:** `observers/IKitchenSubject`, `observers/IKitchenObserver`
- **Builder:**
- **Factory:**

## Dependency injection

`Kitchen` gets its stations, `Clock`, `IExpoPolicy` and `IKitchenSubject` through its constructor and builds none of them. `Rush.main` is the only place they get created.

## Persisted state

Menu + rush scenario load from JSON in `src/main/resources/scenarios/`. Every run writes an event log to `rush-events.log`.

