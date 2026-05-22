package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.repository.PromptTemplatePort;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ResolvePromptUseCasePropertyTest {

    @Mock
    private PromptTemplatePort promptTemplatePort;

    private ResolvePromptUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResolvePromptUseCase(promptTemplatePort);
    }

    @DisplayName("GIVEN random valid template name and variables WHEN executing THEN returns Outcome.Success with resolved text")
    @RepeatedTest(100)
    void givenRandomValidNameAndVariables_whenExecuting_thenReturnsSuccess() {
        var templateName = generateValidTemplateName();
        var variables = generateRandomVariableMap();
        var resolvedText = Instancio.create(String.class);

        when(promptTemplatePort.templateExists(templateName)).thenReturn(true);
        when(promptTemplatePort.resolve(templateName, variables)).thenReturn(new Outcome.Success<>(resolvedText));

        Outcome<String> result = useCase.execute(templateName, variables);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<String>) result).value()).isEqualTo(resolvedText);
    }

    @DisplayName("GIVEN random valid template name that does not exist WHEN executing THEN returns Outcome.Failure containing template name")
    @RepeatedTest(100)
    void givenRandomValidNameThatDoesNotExist_whenExecuting_thenReturnsFailureWithTemplateName() {
        var templateName = generateValidTemplateName();

        when(promptTemplatePort.templateExists(templateName)).thenReturn(false);

        Outcome<String> result = useCase.execute(templateName, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains(templateName));
    }

    @DisplayName("GIVEN random blank template name WHEN executing THEN returns Outcome.Failure and port is never called")
    @RepeatedTest(100)
    void givenRandomBlankName_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        var templateName = generateBlankTemplateName();

        Outcome<String> result = useCase.execute(templateName, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN random template name with path traversal WHEN executing THEN returns Outcome.Failure and port is never called")
    @RepeatedTest(100)
    void givenRandomPathTraversalName_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        var templateName = generatePathTraversalTemplateName();

        Outcome<String> result = useCase.execute(templateName, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN random template name with invalid characters WHEN executing THEN returns Outcome.Failure and port is never called")
    @RepeatedTest(100)
    void givenRandomInvalidCharsName_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        var templateName = generateInvalidCharsTemplateName();

        Outcome<String> result = useCase.execute(templateName, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    private String generateValidTemplateName() {
        var firstChar = randomChar("abcdefghijklmnopqrstuvwxyz");
        var remainingLength = Instancio.gen().ints().range(0, 29).get();
        var sb = new StringBuilder();
        sb.append(firstChar);
        var validChars = "abcdefghijklmnopqrstuvwxyz0123456789-";
        for (var i = 0; i < remainingLength; i++) {
            sb.append(randomChar(validChars));
        }
        return sb.toString();
    }

    private Map<String, Object> generateRandomVariableMap() {
        var size = Instancio.gen().ints().range(0, 10).get();
        var map = new HashMap<String, Object>();
        for (var i = 0; i < size; i++) {
            var key = Instancio.gen().string().alphaNumeric().minLength(1).maxLength(20).get();
            var value = Instancio.create(String.class);
            map.put(key, value);
        }
        return map;
    }

    private String generateBlankTemplateName() {
        var choice = Instancio.gen().ints().range(0, 2).get();
        return switch (choice) {
            case 0 -> null;
            case 1 -> "";
            default -> " ".repeat(Instancio.gen().ints().range(1, 10).get());
        };
    }

    private String generatePathTraversalTemplateName() {
        var prefix = Instancio.gen().string().lowerCase().minLength(0).maxLength(5).get();
        var suffix = Instancio.gen().string().lowerCase().minLength(1).maxLength(10).get();
        var separator = Instancio.gen().ints().range(0, 1).get() == 0 ? "/" : "\\";
        return prefix + ".." + separator + suffix;
    }

    private String generateInvalidCharsTemplateName() {
        var invalidChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ !@#$%^&*()_+=[]{}|;':\",./<>?";
        var length = Instancio.gen().ints().range(1, 30).get();
        var sb = new StringBuilder();
        // Ensure at least one invalid character
        sb.append(invalidChars.charAt(Instancio.gen().ints().range(0, invalidChars.length() - 1).get()));
        for (var i = 1; i < length; i++) {
            var allChars = "abcdefghijklmnopqrstuvwxyz0123456789-" + invalidChars;
            sb.append(allChars.charAt(Instancio.gen().ints().range(0, allChars.length() - 1).get()));
        }
        return sb.toString();
    }

    private char randomChar(String chars) {
        var index = Instancio.gen().ints().range(0, chars.length() - 1).get();
        return chars.charAt(index);
    }
}
