package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("SpringAiPromptTemplateAdapter")
@ExtendWith(MockitoExtension.class)
class SpringAiPromptTemplateAdapterTest {

    @Mock
    private ResourceLoader resourceLoader;

    private SpringAiPromptTemplateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringAiPromptTemplateAdapter(resourceLoader);
    }

    @DisplayName("GIVEN template exists on classpath WHEN resolving with matching variables THEN returns Outcome.Success with substituted text")
    @Test
    void givenTemplateExists_whenResolvingWithMatchingVariables_thenReturnsSuccessWithSubstitutedText() throws IOException {
        var templateContent = "Hello {name}, welcome to {place}!";
        var variables = Map.<String, Object>of("name", "Alice", "place", "Cortex");
        mockExistingTemplate("greeting", templateContent);

        Outcome<String> actual = adapter.resolve("greeting", variables);

        assertThat(actual).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<String>) actual).value()).isEqualTo("Hello Alice, welcome to Cortex!");
    }

    @DisplayName("GIVEN template does not exist WHEN resolving THEN returns Outcome.Failure with template name")
    @Test
    void givenTemplateDoesNotExist_whenResolving_thenReturnsFailureWithTemplateName() {
        mockMissingTemplate("nonexistent");

        Outcome<String> actual = adapter.resolve("nonexistent", Map.of());

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) actual).errors()).anyMatch(e -> e.contains("nonexistent"));
    }

    @DisplayName("GIVEN template with placeholder but variable map missing key WHEN resolving THEN returns Outcome.Failure identifying the placeholder")
    @Test
    void givenTemplateMissingVariable_whenResolving_thenReturnsFailureIdentifyingPlaceholder() throws IOException {
        var templateContent = "Hello {name}, your role is {role}!";
        var variables = Map.<String, Object>of("name", "Alice");
        mockExistingTemplate("role-template", templateContent);

        Outcome<String> actual = adapter.resolve("role-template", variables);

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        var errors = ((Outcome.Failure<String>) actual).errors();
        assertThat(errors).anyMatch(e -> e.contains("role-template"));
    }

    @DisplayName("GIVEN template resolved once WHEN resolving same template again THEN ResourceLoader called only once (cache hit)")
    @Test
    void givenTemplateResolvedOnce_whenResolvingSameTemplateAgain_thenResourceLoaderCalledOnlyOnce() throws IOException {
        var templateContent = "Hello {name}!";
        mockExistingTemplate("cached-template", templateContent);

        adapter.resolve("cached-template", Map.<String, Object>of("name", "First"));
        adapter.resolve("cached-template", Map.<String, Object>of("name", "Second"));

        verify(resourceLoader, times(1)).getResource("classpath:prompts/cached-template.txt");
    }

    @DisplayName("GIVEN templates cached WHEN reloadTemplates called THEN cache cleared and next resolve re-reads from classpath")
    @Test
    void givenTemplatesCached_whenReloadTemplatesCalled_thenCacheClearedAndNextResolveReReads() throws IOException {
        var templateContent = "Hello {name}!";
        mockExistingTemplate("reload-template", templateContent);

        adapter.resolve("reload-template", Map.<String, Object>of("name", "First"));
        adapter.reloadTemplates();
        adapter.resolve("reload-template", Map.<String, Object>of("name", "Second"));

        verify(resourceLoader, times(2)).getResource("classpath:prompts/reload-template.txt");
    }

    @DisplayName("GIVEN template exists WHEN calling templateExists THEN returns true")
    @Test
    void givenTemplateExists_whenCallingTemplateExists_thenReturnsTrue() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resourceLoader.getResource("classpath:prompts/existing-template.txt")).thenReturn(resource);

        boolean actual = adapter.templateExists("existing-template");

        assertThat(actual).isTrue();
    }

    @DisplayName("GIVEN template does not exist WHEN calling templateExists THEN returns false")
    @Test
    void givenTemplateDoesNotExist_whenCallingTemplateExists_thenReturnsFalse() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("classpath:prompts/missing-template.txt")).thenReturn(resource);

        boolean actual = adapter.templateExists("missing-template");

        assertThat(actual).isFalse();
    }

    private void mockExistingTemplate(String templateName, String content) throws IOException {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        when(resourceLoader.getResource("classpath:prompts/" + templateName + ".txt")).thenReturn(resource);
    }

    private void mockMissingTemplate(String templateName) {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("classpath:prompts/" + templateName + ".txt")).thenReturn(resource);
    }
}
