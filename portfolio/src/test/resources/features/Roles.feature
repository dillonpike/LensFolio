Feature: UUiv Roles (read)

  Scenario Outline: AC3 - The roles for the user are displayed on the user’s account page.
    Given I am logged in as "<username>"
    When I browse to the account page
    Then My account page displays the following roles: "<roles>"
    Examples:
      | username     | roles                                  |
      | admin        | student, teacher, course administrator |
      | student      | student                                |
      | teacher      | teacher                                |
      | course_admin | course administrator                   |

  Scenario: AC2 - When a new account is registered, a user is given the “student” role.
    Given I can login or register with a username "roles_test"
    When I browse to the account page
    Then My account page displays the following roles: "student"
