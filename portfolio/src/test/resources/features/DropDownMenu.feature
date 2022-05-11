Feature: UUi Registering and logging into a user account

  Scenario: AC12 - Clicking on the photo icon displays a drop-down menu with "logout" as an option.
    Given I am logged in as admin
    When I click on the photo icon
    Then A menu is displayed
    And There is a logout option

  Scenario: AC12 - Clicking on the "logout" option will log me out.
    Given I am logged in as admin
    When I click on the logout button
    Then I am taken to login page

  Scenario: AC12 - When I have logged out I can not access my account page and will be redirected to the log-in page if I try.
    Given I am logged in as admin
    And I log out
    When I try and access my account
    Then I am taken to login page with forbidden error
