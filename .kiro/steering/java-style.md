# Java Coding Style

## `var` (Local Variable Type Inference)

Use `var` when the type is obvious from context. Avoid it when it hurts readability.

### Use `var`

- **Constructor calls** — the type is visible on the right-hand side:
  ```java
  var errors = new ArrayList<String>();
  var validator = new Validator<MyObject>();
  ```
- **Factory methods and builders** where the return type is clear:
  ```java
  var mapper = ObjectMapper.builder().build();
  ```
- **Literals** — type is unambiguous:
  ```java
  var count = 0;
  var name = "cortex";
  ```
- **For-each and indexed loops**:
  ```java
  for (var entry : map.entrySet()) { ... }
  for (var i = 0; i < list.size(); i++) { ... }
  ```
- **Try-with-resources**:
  ```java
  try (var stream = Files.newInputStream(path)) { ... }
  ```

### Avoid `var`

- **When the type isn't obvious** from the right-hand side:
  ```java
  // Bad — what does this return?
  var result = service.process(input);

  // Good — explicit type documents the contract
  Outcome<Task> result = service.process(input);
  ```
- **Diamond operator with `var`** — loses the element type:
  ```java
  // Bad — infers ArrayList<Object>
  var items = new ArrayList<>();

  // Good — type parameter preserved
  var items = new ArrayList<String>();
  ```
- **Return types of generic or overloaded methods** where the inferred type isn't immediately clear.
- **Fields and method parameters** — `var` is only for local variables (language constraint).

### General Principle

If removing the explicit type makes a reviewer pause to figure out what the variable holds, keep the type. If the type is noise repeating what's already visible, use `var`.

## Records

Use records for immutable value objects, DTOs, and data carriers. This project uses them for `Outcome.Success`, `Outcome.Failure`, and similar types.

- Prefer records over classes for types that are just data with no mutable state.
- Use compact constructors for validation and defensive copying:
  ```java
  record Failure<T>(List<String> errors) implements Outcome<T> {
      public Failure {
          errors = Collections.unmodifiableList(new ArrayList<>(errors));
      }
  }
  ```
- Don't add setters or mutable fields to records.
- Records are final — use sealed interfaces to model type hierarchies with record variants.

## Sealed Interfaces

Use sealed interfaces for type-safe algebraic types where you control all implementations.

- Prefer `sealed interface ... permits` over abstract classes when subtypes are pure data:
  ```java
  public sealed interface Outcome<T> permits Outcome.Success, Outcome.Failure { }
  ```
- Use pattern matching with sealed types in `switch` and `instanceof`:
  ```java
  switch (outcome) {
      case Outcome.Success<T> s -> handleSuccess(s.value());
      case Outcome.Failure<T> f -> handleFailure(f.errors());
  }
  ```
- Exhaustive `switch` over sealed types is enforced by the compiler — no default branch needed.

## Null Handling

Avoid returning `null`. Use `Optional`, empty collections, or domain-specific types instead.

- Return `Optional<T>` for single values that may be absent (e.g., repository lookups).
- Return empty `List` / `Set` / `Map` instead of `null` for collections.
- Use `Objects.requireNonNull()` or explicit null checks in constructors and public method entries:
  ```java
  if (rule == null) {
      throw new IllegalArgumentException("Validation rule must not be null");
  }
  ```
- Never use `Optional` as a field, method parameter, or collection element — it's for return types only.

## Collections and Streams

- Prefer `List.of()`, `Set.of()`, `Map.of()` for small immutable collections.
- Use `.toList()` (unmodifiable) over `.collect(Collectors.toList())`:
  ```java
  List<String> names = items.stream()
          .map(Item::name)
          .toList();
  ```
- Prefer streams for transformations and filtering. Use for-loops when logic involves side effects, early exits, or complex mutable state.
- Avoid nested streams — extract inner logic into a method.

## Exceptions

- Use standard JDK exceptions (`IllegalArgumentException`, `IllegalStateException`) for programming errors.
- Create domain-specific exceptions for business rule violations that cross layer boundaries.
- Never catch `Exception` or `Throwable` broadly — catch the most specific type.
- Include meaningful messages:
  ```java
  throw new IllegalArgumentException("Validation rule must not be null");
  ```
- Don't use exceptions for control flow. Use `Outcome` for expected validation failures.

## Method Design

- Keep methods short and focused — one level of abstraction per method.
- Prefer returning values over mutating parameters. When mutation is needed (like `ValidationRule.validate`), document it clearly in Javadoc.
- Use `static` for methods that don't depend on instance state.
- Prefer method references over lambdas when equally readable:
  ```java
  list.stream().map(String::toLowerCase)  // prefer
  list.stream().map(s -> s.toLowerCase()) // avoid
  ```

## Naming

- Classes: `PascalCase` — `CortexApplication`, `Validator`
- Methods and variables: `camelCase` — `addRule`, `pageSize`
- Constants: `UPPER_SNAKE_CASE` — `MAX_RETRIES`
- Packages: all lowercase, no underscores — `be.jsilkens.cortex.domain`
- Booleans: use `is`/`has`/`can`/`should` prefixes — `isValid`, `hasErrors`
- Use case classes: suffix with `UseCase` — `SummarizeTaskUseCase`
- Port interfaces: suffix with `Port` — `LlmPort`, `StoragePort`
- Interfaces: no `I` prefix — `ValidationRule`, not `IValidationRule`

## Javadoc

Keep documentation pragmatic — this is an application, not a published library.

- Class-level Javadoc: a one-liner explaining *what* the class does. Skip if the name is self-explanatory.
- Method-level Javadoc: only when the behavior isn't obvious from the method name and parameters. Skip `@param`/`@return` tags when they'd just repeat the name.
- Do document non-obvious contracts, side effects, or threading concerns.
- `package-info.java` is fine for module-boundary packages but not required for every package.
- Don't write Javadoc just to satisfy a coverage tool — if the code is clear, let it speak for itself.

## Imports

- No wildcard imports (`import java.util.*`). Always use specific imports.
- Organize: `java.*`, then `jakarta.*`, then third-party, then project packages.
- Remove unused imports.

## Lombok

This project uses Lombok 1.18.x across all modules including domain modules.

- `@RequiredArgsConstructor` — use for dependency injection in Spring beans (constructor injection).
- `@Slf4j` — use for logging.
- `@Builder` — use on DTOs and configuration objects.
- `@Getter` / `@Setter` — use sparingly; prefer records for immutable types.
- Avoid `@Data` on JPA entities — its `equals`/`hashCode` can cause issues with lazy loading.
- Lombok is configured as an annotation processor in the parent POM — no per-module setup needed.

## Formatting

- 4-space indentation (no tabs).
- Braces on the same line as the statement (K&R style).
- One blank line between methods. No multiple consecutive blank lines.
- Max line length: aim for 120 characters, hard limit at 140.
