Feature: UUi Registering and logging into a user account

  Scenario: AC1 Part 1 - Assuming I am not already logged in, the application gives me the
  ability to either log in or register (create) a new account.
    Given I am not logged in
    When I am on the login page
    Then I can login or register

  Scenario: AC1 Part 2 - Assuming I am not already logged in, when I am registering,
  the mandatory attributes are clearly marked.
    Given I am not logged in
    When I am on the register page
    Then Mandatory fields are marked

  Scenario Outline: AC2 Part 1 - If I try to log in with a
  username that has not been registered, the system should let me know.
    Given I am on the login page
    When I login with a username <Username>
    And Username is already registered <isAlreadyRegistered>
    Then <Outcome> message occurs
    And Username is logged in <isLoggedIn>
    Examples:
    |Username   |isAlreadyRegistered    |Outcome                                    |isLoggedIn    |
    |"pointy"   |"False"                |"Invalid username, please try again"       |"False"       |
    |"rlh89"    |"True"                 |"Logged in"                                |"True"        |
    |"rlh88"    |"False"                |"Invalid username, please try again"       |"False"       |

  Scenario Outline: AC2 Part 2 - If I try to register an account with a username that is already registered,
  the system should not create the account but let me know.
    Given I am on the register page
    When I register with a username <Username>
    And Username is already registered <isAlreadyRegistered>
    Then <Outcome> message occurs
    And Username is registered <isRegistered>
    Examples:
    |Username   |isAlreadyRegistered    |Outcome                                  |isRegistered    |
    |"pointy"   |"False"                |"Successful registration"                |"True"          |
    |"rlh89"    |"True"                 |"Invalid registration, username taken"   |"False"         |
    |"rlh88"    |"False"                |"Successful registration"                |"True"          |
