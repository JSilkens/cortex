package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.testdata.LlmRequestTestdata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MockLlmAdapter")
class MockLlmAdapterTest {

    private final MockLlmAdapter adapter = new MockLlmAdapter();

    @DisplayName("GIVEN valid request WHEN generating THEN returns Success containing prompt")
    @Test
    void givenValidRequest_whenGenerating_thenReturnsSuccessContainingPrompt() {
        var request = LlmRequestTestdata.fullTestData();

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Success.class);
        assertThat(((LlmResponse.Success) actual).text()).contains(request.getPrompt());
    }

    @DisplayName("GIVEN request with specific model WHEN generating THEN response includes model name")
    @Test
    void givenRequestWithSpecificModel_whenGenerating_thenResponseIncludesModelName() {
        var request = LlmRequestTestdata.validBuilder()
                .model("gemma4:e4b")
                .build();

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Success.class);
        assertThat(((LlmResponse.Success) actual).text()).contains("gemma4:e4b");
    }
}
