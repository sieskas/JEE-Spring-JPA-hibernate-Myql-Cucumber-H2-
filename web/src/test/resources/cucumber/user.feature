Feature: User creation
  As a user
  I want to create a new user
  So that the new user is saved in the database

  Scenario: Create a new user
    Given I have a UserCreateServlet
    When I create a new user with name "Test" and email "test@example.com" and password "password123"
    Then the new user should be saved in the database
