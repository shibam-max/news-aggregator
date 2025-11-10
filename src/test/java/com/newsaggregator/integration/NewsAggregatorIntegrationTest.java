package com.newsaggregator.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsaggregator.model.NewsSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("News Aggregator Integration Tests")
class NewsAggregatorIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("Should search news with GET request")
    void shouldSearchNewsWithGetRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api/v1/news/search")
                .param("keyword", "technology")
                .param("page", "1")
                .param("pageSize", "10")
                .param("offlineMode", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchKeyword", is("technology")))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.pageSize", is(10)))
                .andExpect(jsonPath("$.offlineMode", is(true)))
                .andExpect(jsonPath("$.articles", isA(java.util.List.class)))
                .andExpect(jsonPath("$.executionTimeMs", greaterThan(0)));
    }
    
    @Test
    @DisplayName("Should search news with POST request")
    void shouldSearchNewsWithPostRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        NewsSearchRequest request = NewsSearchRequest.builder()
                .keyword("business")
                .page(1)
                .pageSize(5)
                .city("london")
                .offlineMode(true)
                .build();
        
        mockMvc.perform(post("/api/v1/news/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchKeyword", is("business")))
                .andExpect(jsonPath("$.city", is("london")))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.pageSize", is(5)))
                .andExpect(jsonPath("$.offlineMode", is(true)));
    }
    
    @Test
    @DisplayName("Should return validation error for invalid request")
    void shouldReturnValidationErrorForInvalidRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api/v1/news/search")
                .param("keyword", "")
                .param("page", "0")
                .param("pageSize", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", containsString("Validation")));
    }
    
    @Test
    @DisplayName("Should return health check information")
    void shouldReturnHealthCheckInformation() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/actuator/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")));
    }
}