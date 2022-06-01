Feature: Live Updates on the Details Page

 Scenario Outline: Getting live update messages when editing events
    Given I am logged in as admin
    And event <EventId> exists
    When I browse to the edit edit page for event <EventId>
    And I have the details page open on another tab
    And I edit an event <EventId>
    Then a live notification appears on the details page with correct message <Message>
   Examples:
     |EventId  | Message            |
     |2        | "Is being edited"  |

  Scenario Outline: Getting live update messages when saving edited events
    Given I am logged in as admin
    And event <EventId> exists
    When I browse to the edit edit page for event <EventId>
    And I have the details page open on another tab
    And I save an event <EventId>
    Then a live notification appears on the details page with correct message <Message>
    Examples:
      |EventId  | Message            |
      |2        | "Has been saved"   |

  Scenario Outline: Live update messages disappearing when not editing events
    Given I am logged in as admin
    And event <EventId> exists
    When I browse to the edit edit page for event <EventId>
    And I have the details page open on another tab
    And I edit an event <EventId>
    And I stop editing an event <EventId>
    Then a live notification disappears from the details page after 5 seconds
    Examples:
      |EventId  |
      |2        |