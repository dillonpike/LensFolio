Feature: UUiii User Profile Photo

  Scenario: AC5 - I can delete my profile photo.
    Given I am logged in as admin
    And I am on the edit account page
    And My profile photo is not the default photo
    When I click the delete photo button
    Then My profile photo is the default photo