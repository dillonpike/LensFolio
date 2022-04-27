Feature: UUv: List of Users

  Scenario: AC1 - Once I am logged in, I can browse to the page containing a list of all users.
    Given I am logged as a user
    When I browse to list of user page
    Then I can see the page that contains list of users registered

  Scenario: AC5 - The sort field and order will persist when I log out and log back in again.
    Given I am logged in as admin
    And I am on the list of users page
    When I sort the list of users by username descending
    And I log out
    And I log in as admin
    And I browse to the list of users page
    Then The list of users is sorted by username descending

#  Scenario Outline: Withdraw money
#    Given I have <Balance> in the account
#    When I choose to withdraw the fixed amount of <Withdrawal>
#    Then I should receive <Received> cash
#    And the balance of my account should be <Remaining>
#    And <Outcome> message should be displayed
#    Examples:
#    |Balance|Withdrawal|Received|Remaining|Outcome|
#    |0.0    |20.0        |0.0   |0.0      |"Not enough funds available"|
#
#  Scenario Outline: Change account name
#    Given I have an account with a name <InitName>
#    When I change the name of the account to <NewName>
#    Then The account name has changed to <ChangedName>
#    And <Outcome> message should be displayed
#    Examples:
#    |InitName |NewName  |ChangedName  |Outcome|
#    |"Larry"  |"John"   |"John"       |"Name changed successfully"|
#    |"Piper"  |"800High"|"Piper"      |"Name change failed, new name contained numbers"|
