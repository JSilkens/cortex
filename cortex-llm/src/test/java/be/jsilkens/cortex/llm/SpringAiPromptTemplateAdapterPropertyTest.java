package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class SpringAiPromptTemplateAdapterPropertyTest {

    @Mock
    private ResourceLoader resourceLoader;

    private SpringAiPromptTemplateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringAiPromptTemplateAdapter(resourceLoader);
    }

    @DisplayName("GIVEN random template with 1-5 placeholders and matching variables WHEN resolving THEN output contains each substituted value")
    @RepeatedTest(100)
    void givenRandomTemplateWithPlaceholders_whenResolving_thenOutputContainsEachSubstitutedValue() throws IOException {
        var varCount = Instancio.gen().ints().range(1, 5).get();
        var varNames = new ArrayList<String>();
        var variables = new HashMap<String, Object>();
        var templateBuilder = new StringBuilder("Template start ");

        for (var i = 0; i < varCount; i++) {
            var varName = generateVariableName(i);
            var varValue = "value" + Instancio.gen().string().alphaNumeric().minLength(3).maxLength(15).get();
            varNames.add(varName);
            variables.put(varName, varValue);
            templateBuilder.append("text ").append("{").append(varName).append("} ");
        }
        templateBuilder.append("end");

        var templateContent = templateBuilder.toString();
        mockExistingTemplate("test-template", templateContent);

        Outcome<String> result = adapter.resolve("test-template", variables);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        var resolvedText = ((Outcome.Success<String>) result).value();
        for (var varName : varNames) {
            assertThat(resolvedText).contains((String) variables.get(varName));
        }
    }

    @DisplayName("GIVEN random template name resolved twice WHEN checking ResourceLoader calls THEN ResourceLoader is called exactly once")
    @RepeatedTest(100)
    void givenRandomTemplateName_whenResolvedTwice_thenResourceLoaderCalledOnce() throws IOException {
        var templateName = generateValidTemplateName();
        var templateContent = "static content";
        mockExistingTemplate(templateName, templateContent);

        adapter.resolve(templateName, Map.of());
        adapter.resolve(templateName, Map.of());

        verify(resourceLoader, times(1)).getResource("classpath:prompts/" + templateName + ".txt");
    }

    @DisplayName("GIVEN random template name resolved then reloaded WHEN resolving again THEN ResourceLoader is called twice")
    @RepeatedTest(100)
    void givenRandomTemplateNameResolvedThenReloaded_whenResolvingAgain_thenResourceLoaderCalledTwice() throws IOException {
        var templateName = generateValidTemplateName();
        var templateContent = "static content";
        mockExistingTemplate(templateName, templateContent);

        adapter.resolve(templateName, Map.of());
        adapter.reloadTemplates();
        adapter.resolve(templateName, Map.of());

        verify(resourceLoader, times(2)).getResource("classpath:prompts/" + templateName + ".txt");
    }

    private String generateVariableName(int index) {
        var prefixes = "abcdefghijklmnopqrstuvwxyz";
        var prefix = String.valueOf(prefixes.charAt(index % prefixes.length()));
        var suffix = Instancio.gen().string().lowerCase().minLength(2).maxLength(8).get();
        return prefix + suffix;
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

    private char randomChar(String chars) {
        var index = Instancio.gen().ints().range(0, chars.length() - 1).get();
        return chars.charAt(index);
    }

    private void mockExistingTemplate(String templateName, String content) throws IOException {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenAnswer(invocation ->
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        when(resourceLoader.getResource("classpath:prompts/" + templateName + ".txt")).thenReturn(resource);
    }
}
