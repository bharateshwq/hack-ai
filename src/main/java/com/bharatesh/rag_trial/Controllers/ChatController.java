package com.bharatesh.rag_trial.Controllers;

import com.bharatesh.rag_trial.Config.PythonFilterProperties;
import com.bharatesh.rag_trial.Models.RagConfigStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatClient chatClient;
    private final RagConfigStore configStore;
    private final WebClient webClient;
    private final PythonFilterProperties pythonFilterProperties;

    public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, RagConfigStore configStore, WebClient webClient, PythonFilterProperties pythonFilterProperties) {
        this.configStore = configStore;
        this.webClient = webClient;
        this.pythonFilterProperties = pythonFilterProperties;

        var qa = QuestionAnswerAdvisor.builder(vectorStore).build();
        this.chatClient = builder.defaultAdvisors(qa).build();
    }


    @GetMapping("/chat")
    public Map<String, Object> chat(@RequestParam String message) {
        boolean hasContext = configStore.isConfigAvailable();
        String response = chatClient.prompt().user(message).call().content();
        return Map.of("ai_response", response, "response_from_context", hasContext);
    }

    @GetMapping("/test-filter")
    public Map<String, Object> testFilter() {

        String url = pythonFilterProperties.getBaseUrl() + "/filter";

        Map<String, String> requestBody = Map.of("text", "hello from spring");

        String pythonResponse = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Map.of(
                "python_response", pythonResponse,
                "status", "success"
        );
    }

}
