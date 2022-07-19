# Currently all actions that update the calendar are using the same STOMP websocket setup.
  # Therefore currently testing for adding is the same as for deleting and editing.
Feature: Live Updates on the Calendar Page

  Scenario: A user adds a event from the project pages.
    Given I am logged in as admin
    And I am on the project page
    And I have the calendar page open on another tab
    And I know about the events on the page
    When I add an event
    And I switch back to the calendar page
    Then the page is reloaded correctly

  Scenario: A user is kept on the page that is not the default calendar page when a event is load.
    Given I am logged in as admin
    And I am on the project page
    And I have the calendar page open on another tab
    And Im not on the default calendar page
    When I add an event
    And I switch back to the calendar page
    Then I remain on the calendar same page.