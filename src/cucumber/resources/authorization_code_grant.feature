Feature: The authorization code grant type is used to obtain both access tokens and refresh tokens and is optimized for confidential clients.
  Since this is a redirection-based flow, the client must be capable of interacting with the resource owner's user-agent (typically a web
  browser) and capable of receiving incoming requests (via redirection)
  from the authorization server.

  (A)  The client initiates the flow by directing the resource owner's user-agent to the authorization endpoint.  The client includes
  its client identifier, requested scope, local state, and a redirection URI to which the authorization server will send the
  user-agent back once access is granted (or denied).

  (B)  The authorization server authenticates the resource owner (via the user-agent) and establishes whether the resource owner
  grants or denies the client's access request.

  (C)  Assuming the resource owner grants access, the authorization server redirects the user-agent back to the client using the
  redirection URI provided earlier (in the request or during client registration). The redirection URI includes an
  authorization code and any local state provided by the client earlier.

  (D)  The client requests an access token from the authorization server's token endpoint by including the authorization code
  received in the previous step.  When making the request, the client authenticates with the authorization server. The client
  includes the redirection URI used to obtain the authorization code for verification.

  (E)  The authorization server authenticates the client, validates the authorization code, and ensures that the redirection URI
  received matches the URI used to redirect the client in step (C).  If valid, the authorization server responds back with
  an access token and, optionally, a refresh token.

  Scenario: The client obtains an Authorization Code
    Given a valid Client:
      | id    | secret | redirectionUri         |
      | myapp | test   | http://test.com/mypath |
    And a valid Resource Owner:
      | username | password | displayName   |
      | owner    | test2    | ayeye brazorf |
    And the client redirects the resource owner to the Authorization Server at the path "/authorization"
    And the query string contains the parameters:
      | response_type | client_id | redirect_uri             |
      | code          | myapp     | http://mydomain/callback |
    And the resource owner uses the basic authentication scheme
    And and the authorization server authenticates the resource owner
    When the resource owner grants access to the client
    Then the resource owner is redirected to the client
    And the client receives the authorization token as a uri fragment

  Scenario: The client obtains an Access Token
    Given a valid Client:
      | id    | secret | redirectionUri         |
      | myapp | test   | http://test.com/mypath |
    And a valid Resource Owner:
      | username | password | displayName   |
      | owner    | test2    | ayeye brazorf |
    And the client has obtained an Authorization Code already
    When the client requests an access token at the path "/token" including the authorization token and the redirect URI
    Then the authorization server authenticates the client
    And verifies the authorization code and the redirection URI matches the one used to obtain it
    And the authorization server responds with an access token