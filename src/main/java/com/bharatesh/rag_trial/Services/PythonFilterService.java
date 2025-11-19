package com.bharatesh.rag_trial.Services;

import com.bharatesh.rag_trial.dto.FilterRequest;
import com.bharatesh.rag_trial.dto.FilterResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PythonFilterService {

    private final WebClient webClient;
    private final String baseUrl;

    public PythonFilterService(WebClient webClient,
                               @Value("${python.filter-service.base-url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public String filterSensitiveData(String text) {
        FilterRequest request = new FilterRequest(text);

        FilterResponse response = webClient.post()
                .uri(baseUrl + "/filter")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FilterResponse.class)
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(2))
                                .filter(ex -> true) // retry for all errors
                )
                .block(Duration.ofSeconds(10)); // timeout

        if (response == null) {
            throw new RuntimeException("Python filter service returned null");
        }

        return response.filteredText();
    }
}
