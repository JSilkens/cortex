package be.jsilkens.cortex.domain.testdata;

import be.jsilkens.cortex.domain.LlmRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LlmRequestTestdata {

    public static LlmRequest fullTestData() {
        return LlmRequest.builder()
                .prompt("Summarize this meeting")
                .model("gemma4:26b")
                .temperature(0.2)
                .maxTokens(4096)
                .build();
    }

    public static LlmRequest.LlmRequestBuilder validBuilder() {
        return LlmRequest.builder()
                .prompt("Summarize this meeting")
                .model("gemma4:26b")
                .temperature(0.2)
                .maxTokens(4096);
    }

    public static LlmRequest randomValid() {
        var prompt = Instancio.gen().string().minLength(1).maxLength(500).get();
        var model = Instancio.gen().oneOf("gemma4:26b", "gemma4:e4b", "gemma4:31b").get();
        var temperature = Instancio.gen().doubles().range(0.0, 2.0).get();
        var maxTokens = Instancio.gen().ints().range(1, 8192).get();
        return LlmRequest.builder()
                .prompt(prompt)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }
}
