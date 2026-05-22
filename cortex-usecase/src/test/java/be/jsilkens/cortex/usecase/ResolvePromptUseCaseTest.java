package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.repository.PromptTemplatePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolvePromptUseCaseTest {

    @Mock
    private PromptTemplatePort promptTemplatePort;

    private ResolvePromptUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResolvePromptUseCase(promptTemplatePort);
    }

    @DisplayName("GIVEN valid template name and variables WHEN executing THEN returns Outcome.Success with resolved text")
    @Test
    void givenValidNameAndVariables_whenExecuting_thenReturnsSuccess() {
        var templateName = "meeting-summary";
        var variables = Map.<String, Object>of("topic", "Q4 review");
        var resolvedText = "Summarize the meeting about Q4 review";

        when(promptTemplatePort.templateExists(templateName)).thenReturn(true);
        when(promptTemplatePort.resolve(templateName, variables)).thenReturn(new Outcome.Success<>(resolvedText));

        Outcome<String> result = useCase.execute(templateName, variables);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<String>) result).value()).isEqualTo(resolvedText);
        verify(promptTemplatePort).templateExists(templateName);
        verify(promptTemplatePort).resolve(templateName, variables);
    }

    @DisplayName("GIVEN valid template name and null variables WHEN executing THEN delegates to port with empty map")
    @Test
    void givenValidNameAndNullVariables_whenExecuting_thenDelegatesToPortWithEmptyMap() {
        var templateName = "plan-day";
        var resolvedText = "Plan your day";

        when(promptTemplatePort.templateExists(templateName)).thenReturn(true);
        when(promptTemplatePort.resolve(templateName, Map.of())).thenReturn(new Outcome.Success<>(resolvedText));

        Outcome<String> result = useCase.execute(templateName, null);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<String>) result).value()).isEqualTo(resolvedText);
        verify(promptTemplatePort).resolve(templateName, Map.of());
    }

    @DisplayName("GIVEN template that does not exist WHEN executing THEN returns Outcome.Failure containing template name")
    @Test
    void givenTemplateDoesNotExist_whenExecuting_thenReturnsFailureWithTemplateName() {
        var templateName = "non-existent";

        when(promptTemplatePort.templateExists(templateName)).thenReturn(false);

        Outcome<String> result = useCase.execute(templateName, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("non-existent"));
        verify(promptTemplatePort).templateExists(templateName);
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN null template name WHEN executing THEN returns Outcome.Failure without calling port")
    @Test
    void givenNullTemplateName_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        Outcome<String> result = useCase.execute(null, Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("must not be blank"));
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN blank template name WHEN executing THEN returns Outcome.Failure without calling port")
    @Test
    void givenBlankTemplateName_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        Outcome<String> result = useCase.execute("   ", Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("must not be blank"));
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN template name with path traversal WHEN executing THEN returns Outcome.Failure without calling port")
    @Test
    void givenTemplateNameWithPathTraversal_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        Outcome<String> result = useCase.execute("../etc/passwd", Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("path traversal"));
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN template name with uppercase letters WHEN executing THEN returns Outcome.Failure without calling port")
    @Test
    void givenTemplateNameWithUppercase_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        Outcome<String> result = useCase.execute("MeetingSummary", Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("lowercase letters, digits, and hyphens"));
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }

    @DisplayName("GIVEN template name with spaces WHEN executing THEN returns Outcome.Failure without calling port")
    @Test
    void givenTemplateNameWithSpaces_whenExecuting_thenReturnsFailureWithoutCallingPort() {
        Outcome<String> result = useCase.execute("meeting summary", Map.of());

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .anyMatch(error -> error.contains("lowercase letters, digits, and hyphens"));
        verify(promptTemplatePort, never()).templateExists(anyString());
        verify(promptTemplatePort, never()).resolve(anyString(), anyMap());
    }
}
