@component
Feature: Gym CRM training and workload publishing
  As the main gym-crm microservice
  I want training lifecycle events to publish correct workload messages
  So that the secondary microservice can track trainer hours

  @positive @trainings
  Scenario: Creating a training publishes an ADD workload message
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    When I create a training with duration 75 for the registered users
    Then the response status should be 200
    And a workload message should be published with actionType "ADD" and duration 75

  @negative @trainings @permissions
  Scenario: Creating a training without authentication is rejected
    Given a registered trainee and trainer for cucumber component tests
    When I create a training with duration 60 for the registered users without authentication
    Then the response status should be 401
    And no workload message should be published

  @negative @trainings
  Scenario: Creating a training for a missing trainer is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    When I create a training for trainee with a missing trainer username
    Then the response status should be 404
    And no workload message should be published

  @negative @trainings
  Scenario: Creating a training for a missing trainee is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    When I create a training for missing trainee with the registered trainer duration 40
    Then the response status should be 404
    And no workload message should be published

  @negative @trainings
  Scenario: Creating a training with inactive trainee is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    And I deactivate the registered trainee
    When I create a training with duration 50 for the registered users
    Then the response status should be 400
    And no workload message should be published

  @negative @trainings
  Scenario: Creating a training with inactive trainer is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    And I deactivate the registered trainer
    When I create a training with duration 50 for the registered users
    Then the response status should be 400
    And no workload message should be published

  @negative @trainings
  Scenario: Creating a training with invalid duration is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    When I create a training with duration 0 for the registered users
    Then the response status should be 400
    And no workload message should be published

  @positive @trainees @trainings
  Scenario: Deleting trainee with a training publishes DELETE workload message
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    And I create a training with duration 55 for the registered users
    And I drain the workload queue
    When I delete the registered trainee
    Then the response status should be 200
    And a workload message should be published with actionType "DELETE" and duration 55

  @positive @trainees
  Scenario: Deleting trainee without trainings publishes no workload message
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    And I drain the workload queue
    When I delete the registered trainee
    Then the response status should be 200
    And no workload message should be published

  @negative @trainees
  Scenario: Deleting unknown trainee is rejected
    Given a registered trainee and trainer for cucumber component tests
    And I am authenticated as the registered trainee
    When I delete trainee username "unknown.trainee.delete"
    Then the response status should be 404
