Feature: U12 Project Details

  Scenario: AC1 - As a user, I can browse to a page that contains the project details
    Given I am logged in as admin
    When I browse to the project page
    Then I can view a page with details of the project

  Scenario: AC2 - As a teacher, I can create and add all the details for a project.
  Appropriate validation is always applied. All changes are persistent.
    Given I am logged in as "Teacher"
    When I browse to the project page
    Then I can create and add all details for a project