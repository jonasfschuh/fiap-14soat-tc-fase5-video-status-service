Feature: Video Status Query

  Scenario: Successfully get status of an existing video
    Given a video "test.mp4" was uploaded by user "user-123" with status "PROCESSING"
    When the user "user-123" requests the video status
    Then the response status should be 200
    And the video status should be "PROCESSING"

  Scenario: Reject access to a video owned by another user
    Given a video "test.mp4" was uploaded by user "user-123" with status "PENDING"
    When the user "other-user" requests the video status
    Then the response status should be 403

  Scenario: Return 404 for a non-existing video
    When the user "user-123" requests a random non-existing video
    Then the response status should be 404

  Scenario: List videos for a user
    Given the user "user-123" has previously uploaded videos
    When the user requests their video list
    Then the response status should be 200
    And the response should contain a list of videos
