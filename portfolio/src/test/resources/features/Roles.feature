Feature: UUiv Roles (read)

  Scenario: AC3 - The roles for the user are displayed on the user’s account page.
    Given I am logged in as admin
    When I browse to the account page
    Then My account page displays the following roles:
    | student              |
    | teacher              |
    | course administrator |