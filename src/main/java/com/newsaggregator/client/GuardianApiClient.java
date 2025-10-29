package com.newsaggregator.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsaggregator.model.NewsArticle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuardianApiClient {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${guardian.api.key}")
    private String apiKey;
    
    @Value("${guardian.api.url}")
    private String baseUrl;
    
    public Mono<List<NewsArticle>> searchNews(String keyword, int page, int pageSize) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("content.guardianapis.com")
                        .path("/search")
                        .queryParam("q", keyword)
                        .queryParam("page", page)
                        .queryParam("page-size", pageSize)
                        .queryParam("show-fields", "headline,trailText,thumbnail,byline")
                        .queryParam("api-key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGuardianResponse)
                .onErrorReturn(new ArrayList<>());
    }
    
    private List<NewsArticle> parseGuardianResponse(String response) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("response").path("results");
            
            for (JsonNode article : results) {
                NewsArticle newsArticle = NewsArticle.builder()
                        .id("guardian_" + article.path("id").asText())
                        .title(article.path("fields").path("headline").asText(article.path("webTitle").asText()))
                        .description(article.path("fields").path("trailText").asText())
                        .url(article.path("webUrl").asText())
                        .source("The Guardian")
                        .publishedAt(parseDate(article.path("webPublicationDate").asText()))
                        .imageUrl(article.path("fields").path("thumbnail").asText())
                        .author(article.path("fields").path("byline").asText())
                        .section(article.path("sectionName").asText())
                        .build();
                articles.add(newsArticle);
            }
        } catch (Exception e) {
            log.error("Error parsing Guardian API response", e);
        }
        return articles;
    }
    
    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}