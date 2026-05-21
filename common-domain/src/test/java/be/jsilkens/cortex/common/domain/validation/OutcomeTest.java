package be.jsilkens.cortex.common.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Outcome")
class OutcomeTest {

    @Nested
    @DisplayName("Success")
    class SuccessTests {

        @Test
        @DisplayName("map transforms the value")
        void mapTransformsValue() {
            Outcome<String> outcome = new Outcome.Success<>("hello");

            Outcome<Integer> mapped = outcome.map(String::length);

            assertThat(mapped).isInstanceOf(Outcome.Success.class);
            assertThat(((Outcome.Success<Integer>) mapped).value()).isEqualTo(5);
        }

        @Test
        @DisplayName("flatMap chains to next outcome on success")
        void flatMapChainsOnSuccess() {
            Outcome<String> outcome = new Outcome.Success<>("42");

            Outcome<Integer> result = outcome.flatMap(s -> new Outcome.Success<>(Integer.parseInt(s)));

            assertThat(result).isInstanceOf(Outcome.Success.class);
            assertThat(((Outcome.Success<Integer>) result).value()).isEqualTo(42);
        }

        @Test
        @DisplayName("flatMap chains to failure when mapper returns failure")
        void flatMapChainsToFailure() {
            Outcome<String> outcome = new Outcome.Success<>("invalid");

            Outcome<Integer> result = outcome.flatMap(s -> new Outcome.Failure<>("Parse error"));

            assertThat(result).isInstanceOf(Outcome.Failure.class);
            assertThat(((Outcome.Failure<Integer>) result).errors()).containsExactly("Parse error");
        }

        @Test
        @DisplayName("toOptional returns Optional.of(value)")
        void toOptionalReturnsValue() {
            Outcome<String> outcome = new Outcome.Success<>("present");

            Optional<String> optional = outcome.toOptional();

            assertThat(optional).isPresent().contains("present");
        }

        @Test
        @DisplayName("toOptional with null value returns Optional.empty()")
        void toOptionalWithNullReturnsEmpty() {
            Outcome<String> outcome = new Outcome.Success<>(null);

            Optional<String> optional = outcome.toOptional();

            assertThat(optional).isEmpty();
        }
    }

    @Nested
    @DisplayName("Failure")
    class FailureTests {

        @Test
        @DisplayName("map preserves the failure")
        void mapPreservesFailure() {
            Outcome<String> outcome = new Outcome.Failure<>("something went wrong");

            Outcome<Integer> mapped = outcome.map(String::length);

            assertThat(mapped).isInstanceOf(Outcome.Failure.class);
            assertThat(((Outcome.Failure<Integer>) mapped).errors()).containsExactly("something went wrong");
        }

        @Test
        @DisplayName("flatMap preserves the failure")
        void flatMapPreservesFailure() {
            Outcome<String> outcome = new Outcome.Failure<>("original error");

            Outcome<Integer> result = outcome.flatMap(s -> new Outcome.Success<>(42));

            assertThat(result).isInstanceOf(Outcome.Failure.class);
            assertThat(((Outcome.Failure<Integer>) result).errors()).containsExactly("original error");
        }

        @Test
        @DisplayName("toOptional returns Optional.empty()")
        void toOptionalReturnsEmpty() {
            Outcome<String> outcome = new Outcome.Failure<>("error");

            Optional<String> optional = outcome.toOptional();

            assertThat(optional).isEmpty();
        }

        @Test
        @DisplayName("single message constructor creates list with one error")
        void singleMessageConstructor() {
            var failure = new Outcome.Failure<String>("single error");

            assertThat(failure.errors()).containsExactly("single error");
        }

        @Test
        @DisplayName("errors list is unmodifiable")
        void errorsListIsUnmodifiable() {
            var failure = new Outcome.Failure<String>(List.of("error1", "error2"));

            org.assertj.core.api.Assertions.assertThatThrownBy(() -> failure.errors().add("new"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("merge")
    class MergeTests {

        @Test
        @DisplayName("with all successes returns Success with provided value")
        void allSuccessesReturnSuccess() {
            Outcome<String> o1 = new Outcome.Success<>("a");
            Outcome<Integer> o2 = new Outcome.Success<>(1);

            Outcome<String> merged = Outcome.merge("combined", o1, o2);

            assertThat(merged).isInstanceOf(Outcome.Success.class);
            assertThat(((Outcome.Success<String>) merged).value()).isEqualTo("combined");
        }

        @Test
        @DisplayName("with any failure returns Failure with combined errors")
        void anyFailureReturnsCombinedFailure() {
            Outcome<String> o1 = new Outcome.Failure<>("error A");
            Outcome<Integer> o2 = new Outcome.Success<>(1);
            Outcome<Boolean> o3 = new Outcome.Failure<>("error B");

            Outcome<String> merged = Outcome.merge("value", o1, o2, o3);

            assertThat(merged).isInstanceOf(Outcome.Failure.class);
            assertThat(((Outcome.Failure<String>) merged).errors())
                    .containsExactly("error A", "error B");
        }

        @Test
        @DisplayName("with single failure returns that failure's errors")
        void singleFailureReturnsItsErrors() {
            Outcome<String> o1 = new Outcome.Success<>("ok");
            Outcome<Integer> o2 = new Outcome.Failure<>(List.of("err1", "err2"));

            Outcome<String> merged = Outcome.merge("value", o1, o2);

            assertThat(merged).isInstanceOf(Outcome.Failure.class);
            assertThat(((Outcome.Failure<String>) merged).errors())
                    .containsExactly("err1", "err2");
        }

        @Test
        @DisplayName("with no outcomes returns Success")
        void noOutcomesReturnsSuccess() {
            Outcome<String> merged = Outcome.merge("value");

            assertThat(merged).isInstanceOf(Outcome.Success.class);
            assertThat(((Outcome.Success<String>) merged).value()).isEqualTo("value");
        }
    }
}
