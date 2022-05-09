Feature: UPi Project Details

  Scenario: AC1 - As a user, I can browse to a page that contains the project details
    Given I am logged in as admin
    When I browse to the project page
    Then I can view a page with details of the project

  Scenario: AC2 - As a teacher, I can create and add all the details for a project.
  Appropriate validation is always applied. All changes are persistent.
    Given I am logged in as "teacher"
    When I browse to the project page
    Then I can create and add all details for a project

  Scenario: AC7 - Default sprint start date is day after the previous sprint end.
    Default sprint end date is 3 weeks after the default start date.
    Given I am logged in as "teacher"
    And I am on the project page
    And There is a sprint
    When I browse to the add sprint page
    Then The start date should be one day after the end date of the previous sprint
    And The end date should be 3 weeks after the start date