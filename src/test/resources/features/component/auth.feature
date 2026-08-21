@component
Feature: Gym CRM authentication
  As a gym CRM user
  I want login and password flows to be secure
  So that only valid users can access protected APIs

  @positive
  Scenario: Successful login returns a JWT token
    Given a registered trainee named "Auth" "Success"
    When I login with the registered trainee credentials
    Then the response status should be 200
    And the response should contain a JWT token

  @negative
  Scenario: Login with wrong password is rejected
    Given a registered trainee named "Auth" "WrongPass"
    When I login with username of registered trainee and password "incorrect-password"
    Then the response status should be 401

  @negative
  Scenario: Login with unknown user is rejected
    When I login with username "ghost.user" and password "any-password123"
    Then the response status should be 401

  @negative
  Scenario: Login with blank password is rejected
    When I login with username "john.doe" and password " "
    Then the response status should be 400

  @negative
  Scenario: Account is locked after three failed login attempts
    Given a registered trainee named "Lock" "Account"
    When I fail login 3 times for the registered trainee
    And I login with the registered trainee credentials
    Then the response status should be 401
    And the response message should contain "locked"

  @positive
  Scenario: Change password with valid old password
    Given a registered trainee named "Pwd" "Change"
    And I am authenticated as the registered trainee
    When I change password from the registered password to "NewPass123!"
    Then the response status should be 200

  @negative
  Scenario: Change password without authentication is rejected
    When I change password from "old" to "new" without authentication
    Then the response status should be 401

  @positive
  Scenario: Logout succeeds for authenticated user
    Given a registered trainee named "Log" "Out"
    And I am authenticated as the registered trainee
    When I logout
    Then the response status should be 200
