# Testing Conventions

## Test Style

Follow the **GIVEN / WHEN / THEN** pattern for all domain and use case tests.

### Naming

- **`@DisplayName`**: Human-readable sentence using `GIVEN ... WHEN ... THEN ...`
- **Method name**: camelCase mirror of the display name — `givenX_whenY_thenZ`

### Structure

Each test method follows this layout:

```java
@DisplayName("GIVEN <precondition> WHEN <action> THEN <expected result>")
@Test
void given<Precondition>_when<Action>_then<ExpectedResult>() {
    // Build the object under test (use testdata builders or inline builders)
    var subject = ...;

    // Execute the behavior
    var actual = subject.someMethod();

    // Assert using AssertJ
    assertThat(actual).isEqualTo(expected);
}
```

### Assertions

- Use **AssertJ** (`assertThat`) for all assertions. Avoid JUnit `assertEquals`/`assertTrue`.
- For `Outcome` validation results:
  - Happy path: `assertThat(actual).isEqualTo(new Outcome.Success<>(subject))`
  - Failure path: check `isInstanceOf(Outcome.Failure.class)` then assert the message `contains(...)` the expected error string.
  - Multiple failures: assert the message `contains(...)` each expected error independently.

### Test Data

- Use a `testdata` subpackage with a `*Testdata` utility class for reusable builders (e.g. `TaskTestdata.fullTestData()`).
- For one-off variations, use Lombok `@Builder` inline in the test to override specific fields.
- `*Testdata` classes should have `@NoArgsConstructor(access = AccessLevel.PRIVATE)` to prevent instantiation.

### Coverage Expectations

For domain entities with validation rules, cover at minimum:
1. **Happy path** — all fields valid (use `fullTestData()`)
2. **Minimal valid** — only required fields set
3. **Each rule individually** — one invalid field at a time (null and blank where applicable)
4. **All rules failing** — all required fields invalid, assert all error messages present

### Example

```java
@DisplayName("GIVEN task with null title WHEN validating THEN Failure outcome with title error")
@Test
void givenTaskWithNullTitle_whenValidating_thenOutcomeFailure() {
    var task = Task.builder()
            .title(null)
            .priority(TaskPriority.MEDIUM)
            .status(TaskStatus.TODO)
            .build();

    Outcome<Task> actual = task.validate();

    assertThat(actual).isInstanceOf(Outcome.Failure.class);
    assertThat(((Outcome.Failure<Task>) actual).message())
            .contains("Task title must not be blank");
}
```

## Property-Based Testing with Instancio

Use **Instancio** (`org.instancio:instancio-junit`) for property-based-style testing. Do **not** use jqwik or other PBT frameworks — Instancio is the project standard.

### Setup

Add `instancio-junit` as a test dependency in modules that need property-based tests:

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-junit</artifactId>
    <version>5.5.1</version>
    <scope>test</scope>
</dependency>
```

### Patterns

**Pattern 1: `@RepeatedTest` + `Instancio.of()`** — Generate random instances inside a repeated test:

```java
@ExtendWith(InstancioExtension.class)
class MyPropertyTest {

    @RepeatedTest(100)
    @DisplayName("GIVEN random task WHEN validating with all fields set THEN outcome is success")
    void validationSucceedsForValidTasks() {
        var task = Instancio.of(Task.class)
                .generate(field(Task::title), gen -> gen.string().minLength(1).maxLength(100))
                .generate(field(Task::priority), gen -> gen.enumOf(TaskPriority.class))
                .create();

        // exercise + assert
    }
}
```

**Pattern 2: `@ParameterizedTest` + `@InstancioSource`** — Instancio generates method parameters:

```java
@ParameterizedTest
@InstancioSource(samples = 100)
@DisplayName("GIVEN random prompt WHEN calling LLM THEN response is non-null")
void llmAlwaysReturnsResponse(String prompt) {
    // exercise + assert
}
```

### Conventions

- Annotate test classes with `@ExtendWith(InstancioExtension.class)`
- Use `Instancio.of(Type.class)` with `.generate(field(...), gen -> ...)` to constrain random data
- Use `Instancio.ofList(Type.class).size(n)` for random lists
- Use `Instancio.gen()` for standalone generators (e.g., `Instancio.gen().ints().range(1, 100).get()`)
- Minimum **100 iterations** per property test (via `@RepeatedTest(100)` or `@InstancioSource(samples = 100)`)
- Name property test classes with a `PropertyTest` suffix (e.g., `LlmServicePropertyTest`)
- Use `@DisplayName` following the same GIVEN/WHEN/THEN convention as regular tests

## Frameworks

- **JUnit 5** for test lifecycle (`@Test`, `@DisplayName`, `@RepeatedTest`, `@ParameterizedTest`)
- **AssertJ** for fluent assertions
- **Mockito** for mocking dependencies in use case tests
- **Instancio** (`instancio-junit` 5.5.1) for property-based test data generation
- **Testcontainers** for integration tests requiring PostgreSQL
- **ArchUnit** for architecture rule enforcement (in `application` module)
