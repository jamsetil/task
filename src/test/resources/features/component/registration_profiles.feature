@component
Feature: Gym CRM registration and profiles
  As a gym CRM client
  I want to register and read profiles
  So that users can join the system

  @positive
  Scenario: Register a trainee successfully
    When I register a trainee with firstName "Reg" and lastName "Trainee"
    Then the response status should be 200
    And the response should contain username and password

  @negative
  Scenario: Register trainee with blank first name is rejected
    When I register a trainee with firstName "" and lastName "Trainee"
    Then the response status should be 400

  @positive
  Scenario: Register a trainer with valid specialization
    When I register a trainer with firstName "Reg" lastName "Trainer" specialization "Fitness"
    Then the response status should be 200
    And the response should contain username and password

  @negative
  Scenario: Register trainer with unknown specialization is rejected
    When I register a trainer with firstName "Reg" lastName "BadSpec" specialization "UnknownSport"
    Then the response status should be 404

  @positive
  Scenario: Get trainee profile with authentication
    Given a registered trainee named "Get" "Profile"
    And I am authenticated as the registered trainee
    When I get trainee profile for the registered trainee
    Then the response status should be 200

  @negative
  Scenario: Get trainee profile without authentication is rejected
    Given a registered trainee named "Get" "NoAuth"
    When I get trainee profile for the registered trainee without authentication
    Then the response status should be 401

  @negative
  Scenario: Get unknown trainee profile is rejected
    Given a registered trainee named "Get" "Missing"
    And I am authenticated as the registered trainee
    When I get trainee profile for username "missing.trainee.xyz"
    Then the response status should be 404

  @positive
  Scenario: List training types when authenticated
    Given a registered trainee named "Types" "List"
    And I am authenticated as the registered trainee
    When I get training types
    Then the response status should be 200
    And the training types list should not be empty
