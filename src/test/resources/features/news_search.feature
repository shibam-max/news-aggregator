Feature: News Search and Aggregation
  As a user
  I want to search for news articles
  So that I can get aggregated news from multiple sources

  Background:
    Given the news aggregator service is running

  Scenario: Search for news with keyword in offline mode
    Given I have a search keyword "apple"
    And I want to search in offline mode
    When I search for news
    Then I should receive news articles
    And the response should include pagination information
    And the response should indicate offline mode

  Scenario: Search for business news with pagination
    Given I have a search keyword "business"
    And I want page 1 with page size 2
    And I want to search in offline mode
    When I search for news
    Then I should receive news articles
    And the response should include pagination information
    And the response should have correct page information

  Scenario: Search for technology news
    Given I have a search keyword "technology"
    And I want to search in offline mode
    When I search for news
    Then I should receive news articles
    And the articles should be related to technology
    And the response should include pagination information

  Scenario: Search with city filter
    Given I have a search keyword "london"
    And I want to search in offline mode
    When I search for news
    Then I should receive news articles
    And the response should include pagination information

  Scenario: Verify response structure
    Given I have a search keyword "market"
    And I want to search in offline mode
    When I search for news
    Then I should receive news articles
    And each article should have required fields
    And the response should include execution time
    And the response should include search metadata