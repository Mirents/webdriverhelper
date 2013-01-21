Feature: Google page test
Scenario: Search for "testing" on Google's search page

    Given I open Google Search homepage
    When I search for "testing"
    Then the first heading of the search results contains the word "testing"
