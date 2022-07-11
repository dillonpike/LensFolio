# Testing for student users may be ideal but there seems to be current problems with checking users in the application.
Feature: Events on the project details/event page.

  @EventsNew
  Scenario: AC-1 Creating a new event from the project details page
    Given I am logged in as admin
    And I am on the project page
    When I click the add event button
    And I save an event with the name "Event-Test"
    Then a event with the name "Event-Test" will exist.

  @EventsNew
  Scenario: AC-4 Editing an event and saving a event
    Given I am logged in as admin
    And an event exists with the name "Event-Test-Old"
    When I browse to the edit edit page for an the event named "Event-Test-Old"
    And I change the name to "Event-Test"
    And I save an event
    Then a event with the name "Event-Test" will exist.


  Scenario: AC-5 Deleting an event
    Given I am logged in as admin
    And an event exists with the name "Event-Test"
    When I click delete for the event "Event-Test"
    Then the event "Event-Test" is deleted from the page.

  Scenario: AC-6 User is told number of characters left.
    Given I am logged in as admin
    And I am on the add event page
    When I type "Event-Test" into the events name
    Then I will be told I have only 40 characters left.
