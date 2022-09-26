Feature: U12 - Deleting pieces of evidence

  Scenario:  AC1 - Each piece of evidence has an icon (such as a rubbish bin) that is clickable.
    Given I am logged in
    And I am on the evidence tab
    And I have added a piece of evidence
    When I click on the delete icon
    Then a delete evidence prompt is presented

  Scenario: AC2 - When I click this icon, a prompt appears asking if I want to delete this evidence from my portfolio.
                  The title of the piece of evidence should also appear on the prompt.
    Given I am logged in
    And I am on the evidence tab
    And I have added a piece of evidence
    And I click on the delete icon
    When a delete evidence prompt is presented
    Then the title of the piece of evidence should appear on the prompt