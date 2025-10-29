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
public class NYTimesApiClient {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${nytimes.api.key}")
    private String apiKey;
    
    public Mono<List<NewsArticle>> searchNews(String keyword, int page, int pageSize) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.nytimes.com")
                        .path("/svc/search/v2/articlesearch.json")
                        .queryParam("q", keyword)
                        .queryParam("page", page - 1) // NYT uses 0-based pagination
                        .queryParam("api-key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseNYTimesResponse)
                .onErrorReturn(new ArrayList<>());
    }
    
    private List<NewsArticle> parseNYTimesResponse(String response) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.path("response").path("docs");
            
            for (JsonNode article : docs) {
                String imageUrl = "";
                JsonNode multimedia = article.path("multimedia");
                if (multimedia.isArray() && multimedia.size() > 0) {
                    imageUrl = "https://www.nytimes.com/" + multimedia.get(0).path("url").asText();
                }
                
                NewsArticle newsArticle = NewsArticle.builder()
                        .id("nyt_" + article.path("_id").asText())
                        .title(article.path("headline").path("main").asText())
                        .description(article.path("abstract").asText())
                        .url(article.path("web_url").asText())
                        .source("The New York Times")
                        .publishedAt(parseDate(article.path("pub_date").asText()))
                        .imageUrl(imageUrl)
                        .author(getAuthor(article.path("byline")))
                        .section(article.path("section_name").asText())
                        .build();
                articles.add(newsArticle);
            }
        } catch (Exception e) {
            log.error("Error parsing NYTimes API response", e);
        }
        return articles;
    }
    
    private String getAuthor(JsonNode byline) {
        if (byline.has("original")) {
            return byline.path("original").asText();
        }
        return "";
    }
    
    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}