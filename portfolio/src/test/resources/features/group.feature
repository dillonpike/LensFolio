Feature: U5: Groups and group membership


  Scenario: AC1 - Once I am logged in, I can browse to the group page.
    Given I am logged in as admin
    When I browse to the group page
    Then I can see group list and group table