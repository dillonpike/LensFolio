Feature: U6: Group settings and single repository settings

  Scenario: AC 1: As soon as a group is created (saved), a group settings page exists.
    Given  I am logged in as admin
    When I browse to the group page
    And I click on the teacher group
    And I click the group setting button
    Then I can see the group setting page
