Feature: Account is credited with amount

  Scenario: Credit amount
    Given account balance is 0.0
    When the account is credited with 10.0
    Then account should have a balance of 10.0

  Scenario Outline: Withdraw money
    Given I have <Balance> in the account
    When I choose to withdraw the fixed amount of <Withdrawal>
    Then I should receive <Received> cash
    And the balance of my account should be <Remaining>
    And <Outcome> message should be displayed
    Examples:
    |Balance|Withdrawal|Received|Remaining|Outcome|
    |0.0    |20.0        |0.0   |0.0      |"Not enough funds available"|

  Scenario Outline: Change account name
    Given I have an account with a name <InitName>
    When I change the name of the account to <NewName>
    Then The account name has changed to <ChangedName>
    And <Outcome> message should be displayed
    Examples:
    |InitName |NewName  |ChangedName  |Outcome|
    |"Larry"  |"John"   |"John"       |"Name changed successfully"|
    |"Piper"  |"800High"|"Piper"      |"Name change failed, new name contained numbers"|
