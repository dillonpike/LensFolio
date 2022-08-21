Feature: U7: Piece of evidence creation

  Scenario: AC10 pt.1 - A user can be searched for by name
    Given I am logged in
    And I browse to the user search page
    And The user "admin" exists
    When I search for a user "admin"
    Then the user "admin" is shown in the search results.

  Scenario: AC10 pt.2 - A user can be searched for by name when a user does not exist
    Given I am logged in
    And I browse to the user search page
    And The user "NotARealUsername32897" does not exist
    When I search for a user "NotARealUsername32897"
    Then the user "NotARealUsername32897" is not shown in the search results.


