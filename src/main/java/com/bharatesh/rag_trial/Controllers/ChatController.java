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
import com.bharatesh.rag_trial.dto.ChatDTOs.ConversationalRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.DocsGenRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.FilteredResponse;
import com.bharatesh.rag_trial.dto.ChatDTOs.GeneralChatResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
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

        private final List<Message> converstions;

        public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, RagConfigStore configStore,
                        WebClient webClient, PythonFilterProperties pythonFilterProperties, ChatService chatService) {
                this.configStore = configStore;
                this.webClient = webClient;
                this.pythonFilterProperties = pythonFilterProperties;
                this.chatService = chatService;
                this.converstions = new ArrayList<Message>();
                final String systemMessageString = """
                                You are a senior software consultant trained in Big 4 (Deloitte, EY, KPMG, PwC) engineering review standards.

                                Your ONLY responsibility is to help the user understand the JavaScript code snippet they provide.
                                You must follow enterprise-grade documentation and analysis rules.

                                STRICT RULES — DO NOT BREAK:
                                1. You may ONLY answer questions directly related to the code snippet provided by the user.
                                2. If the user asks anything unrelated to the code (math, life, personal, general knowledge, etc.), always respond with exactly:
                                “I don’t know.”
                                3. Maintain Big 4 technical communication standards:
                                - Be concise, accurate, and objective.
                                - No filler, no opinions, no creativity.
                                - No assumptions beyond what the code clearly shows.
                                4. Never rewrite, refactor, or modify the user’s code unless explicitly asked.
                                5. Never hallucinate missing logic. If the code is incomplete, say:
                                “The provided code does not contain enough information.”
                                6. For documentation requests, produce clean, formal, standardized JSDoc only.
                                7. For explanation requests, respond with structured, technical, code-focused reasoning:
                                - Purpose
                                - Inputs / Outputs
                                - Data flow
                                - Side effects
                                - Potential errors or risks
                                - Edge cases
                                8. Never generate any content outside the requested format.
                                9. Never include warnings, disclaimers, or meta-commentary.
                                10. All responses must be strictly about the code snippet, never about the larger system unless the snippet proves it.

                                CONVERSATIONAL MODE BEHAVIOR:
                                - Maintain context between turns.
                                - Only use information found in the provided code or previous code-related messages.
                                - If the user references earlier code, use it.
                                - If the user asks about something not previously provided, respond with:
                                “I don’t know.”

                               

                                YOU MUST FOLLOW THESE RULES EXACTLY.


                                """;

                final SystemMessage systemMessage = new SystemMessage(systemMessageString);
                this.converstions.add(systemMessage);

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
                                                log.info("Received docs request: " + request);

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

        @PostMapping("/chat/{projectId}/conversational")
        public GeneralChatResponse conversational(@PathVariable Long projectId,
                        @RequestBody ConversationalRequest request) {
                                log.info("Received conversational request: " + request);
                boolean hasContext = configStore.isConfigAvailable();
                List<String> entityNames = chatService.getProjectEntityList(projectId);
                FilteredResponse filteredSnippetResponse = chatService.getSanitizedQuery(request.getCodeSnippet(),
                                entityNames);
                FilteredResponse filteredQueryResponse = chatService.getSanitizedQuery(request.getQuery(), entityNames);

                // STEP 3 — Fetch registry entities to send to Python (if needed)

                // RegexPatternTypeFilter regex = new
                // RegexPatternTypeFilter("\\d{4}-\\d{4}-\\d{4}-\\d{4}", 0.9);

                // CustomEntity customEntity = new CustomEntity(
                // "TITLE",
                // // List.of("SECRET", "PRIVATE"),
                // regex);

                log.info(" Response from python filter service: " + filteredSnippetResponse);
                log.info(" Response from python filter service: " + filteredQueryResponse);

                final Message userMessage = new UserMessage(filteredQueryResponse.getAnonymizedText() + filteredSnippetResponse.getAnonymizedText());
                this.converstions.add(userMessage);
                String modelResponse = chatClient.prompt()
                                .messages(this.converstions)
                                // .system(systemMessage)
                                .call().content();

                final Message assistantMessage = new AssistantMessage(modelResponse);
                this.converstions.add(assistantMessage);

                return new GeneralChatResponse(
                                modelResponse,
                                hasContext,
                                filteredQueryResponse.getAnonymizedText());
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
                                "status", "success");
        }

}
