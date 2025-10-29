package com.newsaggregator.bdd;

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
    
    private NewsSearchRequest searchRequest;
    private NewsSearchResponse searchResponse;
    
    @Given("I have a search keyword {string}")
    public void i_have_a_search_keyword(String keyword) {
        searchRequest = NewsSearchRequest.builder()
                .keyword(keyword)
                .page(1)
                .pageSize(10)
                .offlineMode(true) // Use offline mode for testing
                .build();
    }
    
    @When("I search for news")
    public void i_search_for_news() {
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
    }
}