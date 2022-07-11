# May want to add testing for AC-6/AC-7 but due to AC-7 not being completed currently the implementation is left absent.
  # Also testing for student users may be ideal but there seems to be current problems with checking users in the application.
Feature: Events on the project details/event page.

  Scenario: AC-1 Creating a new event from the project details page
    Given I am logged in as admin
    And I am on the project page
    When I click the add event button
    And I save an event with the name "Event-Test"
    Then the event is created

  Scenario: AC-4 Editing an event and saving a event
    Given I am logged in as admin
    And an event exists with the name "Event-Test"
    When I browse to the edit edit page for an the event named "Event-Test"
    And I change the name to "Event-New-Test"
    And I save an event
    Then a event with the name "Event-Test" will exist.

  Scenario: AC-5 Deleting an event
    Given I am logged in as admin
    And an event exists
    And I am on the project page
    When I click delete event.
    Then the event is deleted from the page.
