Feature: Live Updates on the Calendar Page

  Scenario: A user adds a event from the project pages.
    Given I am logged in as admin
    And I have the calendar page open on another tab
    When I add an event
    Then the page is reloaded correctly

  Scenario: A user edits a event from the project pages.
    Given I am logged in as admin
    And An event exists
    And I have the calendar page open on another tab
    When I edit an event
    Then the page is reloaded correctly

  Scenario: A user removes a event from the project pages.
    Given I am logged in as admin
    And An event exists
    And I have the calendar page open on another tab
    When I remove an event
    Then the page is reloaded correctly

  Scenario: A user is kept on the page that is not the default calendar page when a event is reload.
    Given I am logged in as admin
    And An event exists
    And Im not on the default calendar page
    When I edit an event
    Then I remain on the same page.