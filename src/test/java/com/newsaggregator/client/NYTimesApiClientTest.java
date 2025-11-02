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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NYTimesApiClientTest {
    
    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    private NYTimesApiClient nyTimesApiClient;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        nyTimesApiClient = new NYTimesApiClient(webClient, objectMapper);
        ReflectionTestUtils.setField(nyTimesApiClient, "apiKey", "test-key");
    }
    
    @Test
    void searchNews_ShouldReturnArticles_WhenValidResponse() {
        // Given
        String mockResponse = "{\n" +
                "  \"response\": {\n" +
                "    \"docs\": [\n" +
                "      {\n" +
                "        \"_id\": \"test-nyt-id\",\n" +
                "        \"headline\": {\n" +
                "          \"main\": \"NY Times Test Article\"\n" +
                "        },\n" +
                "        \"abstract\": \"Test abstract from NYT\",\n" +
                "        \"web_url\": \"https://nytimes.com/test\",\n" +
                "        \"pub_date\": \"2024-01-01T10:00:00Z\",\n" +
                "        \"section_name\": \"Business\",\n" +
                "        \"byline\": {\n" +
                "          \"original\": \"By Test Reporter\"\n" +
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
        StepVerifier.create(nyTimesApiClient.searchNews("apple", 1, 10))
                .assertNext(articles -> {
                    assertNotNull(articles);
                    assertEquals(1, articles.size());
                    NewsArticle article = articles.get(0);
                    assertEquals("nyt_test-nyt-id", article.getId());
                    assertEquals("NY Times Test Article", article.getTitle());
                    assertEquals("Test abstract from NYT", article.getDescription());
                    assertEquals("https://nytimes.com/test", article.getUrl());
                    assertEquals("The New York Times", article.getSource());
                })
                .verifyComplete();
    }
    
    @Test
    void searchNews_ShouldReturnEmptyList_WhenApiError() {
        // Given
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("NYT API Error")));
        
        // When & Then
        StepVerifier.create(nyTimesApiClient.searchNews("apple", 1, 10))
                .assertNext(articles -> {
                    assertNotNull(articles);
                    assertTrue(articles.isEmpty());
                })
                .verifyComplete();
    }
}
