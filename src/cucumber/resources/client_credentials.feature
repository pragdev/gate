Feature: The client credentials (or other forms of client authentication) can
  be used as an authorization grant when the authorization scope is
  limited to the protected resources under the control of the client,
  or to protected resources previously arranged with the authorization
  server.  Client credentials are used as an authorization grant
  typically when the client is acting on its own behalf (the client is
  also the resource owner) or is requesting access to protected
  resources based on an authorization previously arranged with the
  authorization server.


  Scenario: The client successfully obtains an Access Token
  (A)  The client authenticates with the authorization server and
  requests an access token from the token endpoint.

  (B)  The authorization server authenticates the client, and if valid,
  issues an access token.
    Given valid Client credentials:
      | username | password |
      | myapp    | test     |
    When the client makes a POST request to the Authorization Server at the path "/token"
    And the request uses the basic authentication scheme
    And the body "application/x-www-form-urlencoded" contains the parameters:
      | grant_type         |
      | client_credentials |
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
