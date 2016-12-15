Feature: The resource owner password credentials (i.e., username and password) can be used directly as
  an authorization grant to obtain an access token. The credentials should only be used when there is
  a high degree of trust between the resource owner and the client (e.g., the client is part of the
  device operating system or a highly privileged application), and when other authorization grant types
  are not available (such as an authorization code).

  Scenario: The authorization server issues an access token
  (A)  The resource owner provides the client with its username and password.

  (B)  The client requests an access token from the authorization server's token endpoint by including
  the credentials received from the resource owner.  When making the request, the client authenticates
  with the authorization server.

  (C)  The authorization server authenticates the client and validates the resource owner credentials,
  and if valid, issues an access token.

    Given a valid Resource Owner:
      | username | password | displayName     |
      | antonio  | test     | antonio brazorf |
    When the client makes a POST request to the Authorization Server at the path "/token"
    And the body "application/x-www-form-urlencoded" contains the parameters:
      | grant_type | username | password |
      | password   | antonio  | test     |
    Then the Authentication Server should respond OK
    And should be non cacheable
    And the content type should be JSON
    And the body should be:
    """
    {
      "access_token":"__non_empty__",
      "token_type":"bearer",
      "expires_in":3600
    }
    """
