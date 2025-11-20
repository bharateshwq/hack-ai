package com.bharatesh.rag_trial.Controllers;

import com.bharatesh.rag_trial.Config.PythonFilterProperties;
import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Models.RagConfigStore;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;
import com.bharatesh.rag_trial.Repositories.RegistryEntityRepository;
import com.bharatesh.rag_trial.Services.ChatService;
import com.bharatesh.rag_trial.dto.ChatDTOs.AnalyzeFilterRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.ChatRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.DocsGenRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.FilteredResponse;
import com.bharatesh.rag_trial.dto.ChatDTOs.GeneralChatResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChatController {
        private final ChatClient chatClient;
        private final RagConfigStore configStore;
        private final WebClient webClient;
        private final PythonFilterProperties pythonFilterProperties;
        @Autowired
        private ProjectSettingRepository projectSettingRepository;

        @Autowired
        private RegistryEntityRepository registryRepo;

        private ChatService chatService;

        public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, RagConfigStore configStore,
                        WebClient webClient, PythonFilterProperties pythonFilterProperties, ChatService chatService) {
                this.configStore = configStore;
                this.webClient = webClient;
                this.pythonFilterProperties = pythonFilterProperties;
                this.chatService = chatService;

                var qa = QuestionAnswerAdvisor.builder(vectorStore).build();
                this.chatClient = builder.defaultAdvisors(qa).build();
        }

        @GetMapping("/chat")
        public Map<String, Object> chat(@RequestParam(required = true) String message) {

                String response = chatClient.prompt().user(message).call().content();
                return Map.of("ai_response", response);
        }

        // text: str
        // language: str = "en" # Default language is English
        // predefined_entities: list[str] = [] # Predefined recognizers to use
        // custom_entities:

        @PostMapping("/chat/{projectId}")
        public GeneralChatResponse chat(@PathVariable Long projectId, @RequestBody ChatRequest message) {
                boolean hasContext = configStore.isConfigAvailable();
                List<String> entityNames = chatService.getProjectEntityList(projectId);
                FilteredResponse filteredResponse = chatService.getSanitizedQuery(message.getQuery(), entityNames);
                // STEP 3 — Fetch registry entities to send to Python (if needed)

                // RegexPatternTypeFilter regex = new
                // RegexPatternTypeFilter("\\d{4}-\\d{4}-\\d{4}-\\d{4}", 0.9);

                // CustomEntity customEntity = new CustomEntity(
                // "TITLE",
                // // List.of("SECRET", "PRIVATE"),
                // regex);
                log.info("Sending request to Python filter service: " + filteredResponse);
                String response = chatClient.prompt().user(filteredResponse.getAnonymizedText()).call().content();
                return new GeneralChatResponse(
                                response,
                                hasContext,
                                filteredResponse.getAnonymizedText());
        }

        @PostMapping("/chat/{projectId}/docs")
        public GeneralChatResponse docsGen(@PathVariable Long projectId, @RequestBody DocsGenRequest request) {
                boolean hasContext = configStore.isConfigAvailable();
                List<String> entityNames = chatService.getProjectEntityList(projectId);
                FilteredResponse filteredResponse = chatService.getSanitizedQuery(request.getCode(), entityNames);

                // STEP 3 — Fetch registry entities to send to Python (if needed)

                // RegexPatternTypeFilter regex = new
                // RegexPatternTypeFilter("\\d{4}-\\d{4}-\\d{4}-\\d{4}", 0.9);

                // CustomEntity customEntity = new CustomEntity(
                // "TITLE",
                // // List.of("SECRET", "PRIVATE"),
                // regex);
                final String systemMessage = """
                                You are an expert technical writer specializing in JavaScript documentation.

                                Your only task is to generate **clean, standardized, and formal JSDoc documentation** for the given JavaScript function or code snippet.

                                STRICT RULES — DO NOT VIOLATE:
                                1. Output ONLY JSDoc documentation. No explanations, no comments, no descriptions outside the JSDoc block.
                                2. Follow standardized JSDoc format exactly:
                                - /** … */
                                - @param for every parameter (with correct type inference)
                                - @returns or @return when required
                                - @async, @deprecated, @throws, etc. when applicable
                                3. Description must be concise, action-oriented, and technical.
                                4. If the function name or params are unclear, infer accurately from usage and still document them.
                                5. Never rewrite or fix code. Only document it.
                                6. Never include placeholders like “TODO”.
                                7. Never generate anything outside the JSDoc block, not even greetings or summaries.

                                Your output format MUST always look like:
                                /**

                                <One-sentence description of the function’s purpose.>
                                Make sure the length of the one liner is less than  %s  vscode editor width.
                                After that go to next line.

                                @param {<Type>} <name> - <description>

                                @param {<Type>} <name> - <description>

                                @returns {<Type>} <description>
                                */


                                If the code has no return value, document it as:
                                @returns {void}

                                If the function is async:
                                @async

                                Failure to follow this EXACT format is not allowed.
                                Give Me Documentation for the following code:
                                 
                                """;
                String finalPrompt = String.format(systemMessage, request.getWidth());

                log.info("Sending request to Python filter service: " + filteredResponse);
                String response = chatClient.prompt()
                                .system(finalPrompt)
                                .user(request.getCode())
                                .call().content();

                return new GeneralChatResponse(
                                response,
                                hasContext,
                                filteredResponse.getAnonymizedText());
        }

        // @PostMapping("/chat/{projectId}/docs")

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
                                "status", "success");
        }

}
