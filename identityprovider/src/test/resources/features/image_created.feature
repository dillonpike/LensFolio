Feature: Add, delete and edit images of a user

  @AC1
  Scenario: Upload an image
    Given I have a user in the database
    When I upload an image
    Then The image is saved persistently

