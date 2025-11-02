package com.newsaggregator.bdd;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import com.newsaggregator.service.NewsAggregatorService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NewsSearchSteps {
    
    @Autowired
    private NewsAggregatorService newsAggregatorService;
    
    private NewsSearchRequest.NewsSearchRequestBuilder requestBuilder;
    private NewsSearchRequest searchRequest;
    private NewsSearchResponse searchResponse;
    
    @Given("the news aggregator service is running")
    public void the_news_aggregator_service_is_running() {
        // Check service
        assertNotNull(newsAggregatorService);
        requestBuilder = NewsSearchRequest.builder();
    }
    
    @Given("I have a search keyword {string}")
    public void i_have_a_search_keyword(String keyword) {
        requestBuilder.keyword(keyword);
    }
    
    @Given("I want to search in offline mode")
    public void i_want_to_search_in_offline_mode() {
        requestBuilder.offlineMode(true);
    }
    
    @Given("I want page {int} with page size {int}")
    public void i_want_page_with_page_size(int page, int pageSize) {
        requestBuilder.page(page).pageSize(pageSize);
    }
    
    @When("I search for news")
    public void i_search_for_news() {
        // Build request with defaults if not set
        if (requestBuilder == null) {
            requestBuilder = NewsSearchRequest.builder();
        }
        
        searchRequest = requestBuilder.build();
        
        // Set defaults if not already set
        if (searchRequest.getPage() == 0) {
            searchRequest = searchRequest.toBuilder().page(1).build();
        }
        if (searchRequest.getPageSize() == 0) {
            searchRequest = searchRequest.toBuilder().pageSize(10).build();
        }
        
        StepVerifier.create(newsAggregatorService.searchNews(searchRequest))
                .assertNext(response -> {
                    this.searchResponse = response;
                })
                .verifyComplete();
    }
    
    @Then("I should receive news articles")
    public void i_should_receive_news_articles() {
        assertNotNull(searchResponse);
        assertNotNull(searchResponse.getArticles());
        assertEquals(searchRequest.getKeyword(), searchResponse.getSearchKeyword());
    }
    
    @Then("the response should include pagination information")
    public void the_response_should_include_pagination_information() {
        assertNotNull(searchResponse.getCurrentPage());
        assertNotNull(searchResponse.getTotalPages());
        assertNotNull(searchResponse.getPageSize());
        assertTrue(searchResponse.getCurrentPage() > 0);
    }
    
    @Then("the response should indicate offline mode")
    public void the_response_should_indicate_offline_mode() {
        assertTrue(searchResponse.isOfflineMode());
    }
    
    @Then("the response should have correct page information")
    public void the_response_should_have_correct_page_information() {
        assertEquals(searchRequest.getPage(), searchResponse.getCurrentPage());
        assertEquals(searchRequest.getPageSize(), searchResponse.getPageSize());
    }
    
    @Then("the articles should be related to technology")
    public void the_articles_should_be_related_to_technology() {
        assertTrue(searchResponse.getArticles().size() > 0);
        // Check for technology content
        boolean hasTechContent = searchResponse.getArticles().stream()
                .anyMatch(article -> 
                    article.getTitle().toLowerCase().contains("technology") ||
                    article.getTitle().toLowerCase().contains("tech") ||
                    article.getDescription().toLowerCase().contains("technology"));
        assertTrue(hasTechContent, "Should contain technology-related articles");
    }
    
    @Then("each article should have required fields")
    public void each_article_should_have_required_fields() {
        assertFalse(searchResponse.getArticles().isEmpty());
        for (NewsArticle article : searchResponse.getArticles()) {
            assertNotNull(article.getId(), "Article should have ID");
            assertNotNull(article.getTitle(), "Article should have title");
            assertNotNull(article.getUrl(), "Article should have URL");
            assertNotNull(article.getSource(), "Article should have source");
            assertNotNull(article.getPublishedAt(), "Article should have publication date");
        }
    }
    
    @Then("the response should include execution time")
    public void the_response_should_include_execution_time() {
        assertNotNull(searchResponse.getExecutionTimeMs());
        assertTrue(searchResponse.getExecutionTimeMs() >= 0);
    }
    
    @Then("the response should include search metadata")
    public void the_response_should_include_search_metadata() {
        assertEquals(searchRequest.getKeyword(), searchResponse.getSearchKeyword());
        assertNotNull(searchResponse.getCurrentPage());
        assertNotNull(searchResponse.getTotalResults());
    }
}