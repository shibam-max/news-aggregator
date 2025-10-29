Feature: News Search and Aggregation
  As a user
  I want to search for news articles
  So that I can get aggregated news from multiple sources

  Scenario: Search for news with keyword
    Given I have a search keyword "apple"
    When I search for news
    Then I should receive news articles
    And the response should include pagination information

  Scenario: Search for news in offline mode
    Given I have a search keyword "technology"
    When I search for news
    Then I should receive news articles
    And the response should include pagination information