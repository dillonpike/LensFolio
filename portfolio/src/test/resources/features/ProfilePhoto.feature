Feature: UUiii User Profile Photo
  Scenario: AC4 - I can see the photo on my account page. A small version of the image is displayed in the header of the page instead of the generic user template icon
    Given I am logged in as admin
    And I browse to the account page
    Then My small version of my profile photo is displayed in the header of the page


  Scenario: AC5 - I can delete my profile photo.
    Given I am logged in as admin
    And I am on the edit account page
    And My profile photo is not the default photo
    When I click the delete photo button
    Then My profile photo is the default photo
