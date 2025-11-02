package com.newsaggregator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsaggregator.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuardianApiClientTest {
    
    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    private GuardianApiClient guardianApiClient;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        guardianApiClient = new GuardianApiClient(webClient, objectMapper);
        ReflectionTestUtils.setField(guardianApiClient, "apiKey", "test-key");
        ReflectionTestUtils.setField(guardianApiClient, "baseUrl", "https://content.guardianapis.com");
    }
    
    @Test
    void searchNews_ShouldReturnArticles_WhenValidResponse() {
        // Given
        String mockResponse = "{\n" +
                "  \"response\": {\n" +
                "    \"results\": [\n" +
                "      {\n" +
                "        \"id\": \"test-id\",\n" +
                "        \"webTitle\": \"Test Article\",\n" +
                "        \"webUrl\": \"https://test.com\",\n" +
                "        \"webPublicationDate\": \"2024-01-01T10:00:00Z\",\n" +
                "        \"sectionName\": \"Technology\",\n" +
                "        \"fields\": {\n" +
                "          \"headline\": \"Test Headline\",\n" +
                "          \"trailText\": \"Test description\",\n" +
                "          \"byline\": \"Test Author\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));
        
        // When & Then
        StepVerifier.create(guardianApiClient.searchNews("apple", 1, 10))
                .assertNext(articles -> {
                    assertNotNull(articles);
                    assertEquals(1, articles.size());
                    NewsArticle article = articles.get(0);
                    assertEquals("guardian_test-id", article.getId());
                    assertEquals("Test Headline", article.getTitle());
                    assertEquals("https://test.com", article.getUrl());
                    assertEquals("The Guardian", article.getSource());
                })
                .verifyComplete();
    }
    
    @Test
    void searchNews_ShouldReturnEmptyList_WhenApiError() {
        // Given
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("API Error")));
        
        // When & Then
        StepVerifier.create(guardianApiClient.searchNews("apple", 1, 10))
                .assertNext(articles -> {
                    assertNotNull(articles);
                    assertTrue(articles.isEmpty());
                })
                .verifyComplete();
    }
}
