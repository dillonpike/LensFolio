Feature: UUv: List of Users

  Scenario: AC1 - Once I am logged in, I can browse to the page containing a list of all users.
    Given I am logged in as admin
    When I browse to the list of users page
    Then I can see a list of users

  Scenario: AC2 - The list is displayed in a table format with name, username, alias, and roles, as columns.
    Given I am logged in as admin
    When I browse to the list of users page
    Then The list of users has the following columns:
    | First Name |
    | Last Name  |
    | Username   |
    | Alias      |
    | Roles      |

  Scenario: AC3 - If I have many users, it should not all display on one page.
    Given I am logged in as admin
    When I browse to the list of users page
    Then The list of users is separated into multiple pages

  Scenario: AC5 - The sort field and order will persist when I log out and log back in again.
    Given I am logged in as admin
    And I am on the list of users page
    When I sort the list of users by username descending
    And I log out
    And I log in as admin
    And I browse to the list of users page
    Then The list of users is sorted by username descending

  Scenario: The page will persist when I add/delete a user role.
    Given I am logged in as admin
    And I am on the list of users page
    And I go to the next page
    When I remove a role
    Then I am on same page

