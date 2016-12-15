Feature:  The implicit grant type is used to obtain access tokens (it does not support the issuance of refresh tokens)
  and is optimized for public clients known to operate a particular redirection URI.  These clients are typically
  implemented in a browser using a scripting language such as JavaScript.

  Since this is a redirection-based flow, the client must be capable of interacting with the resource owner's user-agent
  (typically a web browser) and capable of receiving incoming requests (via redirection) from the authorization server.

  Unlike the authorization code grant type, in which the client makes separate requests for authorization and for an
  access token, the client receives the access token as the result of the authorization request.

  The implicit grant type does not include client authentication, and relies on the presence of the resource owner and
  the registration of the redirection URI.  Because the access token is encoded into the redirection URI, it may be
  exposed to the resource owner and other applications residing on the same device.


  Scenario: A)  The client initiates the flow by directing the resource owner's user-agent to the authorization endpoint.
  The client includes its client identifier, requested scope, local state, and a redirection URI to which the authorization
  server will send the user-agent back once access is granted (or denied).

  (B)  The authorization server authenticates the resource owner (via the user-agent) and establishes whether the
  resource owner grants or denies the client's access request.

  (C)  Assuming the resource owner grants access, the authorization server redirects the user-agent back to the client using the
  redirection URI provided earlier.  The redirection URI includes the access token in the URI fragment.

  (D)  The user-agent follows the redirection instructions by making a request to the web-hosted client resource (which does not
  include the fragment per [RFC2616]).  The user-agent retains the fragment information locally.

  (E)  The web-hosted client resource returns a web page (typically an HTML document with an embedded script) capable of
  accessing the full redirection URI including the fragment retained by the user-agent, and extracting the access token
  (and other parameters) contained in the fragment.

  (F)  The user-agent executes the script provided by the web-hosted client resource locally, which extracts the access token.

  (G)  The user-agent passes the access token to the client.
    Given a valid Client:
      | id    | secret | redirectionUri         |
      | myapp | test   | http://test.com/mypath |
    And a valid Resource Owner:
      | username | password | displayName   |
      | owner    | test2    | ayeye brazorf |
    And the client redirects the resource owner to the Authorization Server at the path "/authorization"
    And the query string contains the parameters:
      | response_type | client_id | redirect_uri             |
      | token         | myapp     | http://mydomain/callback |
    And the resource owner uses the basic authentication scheme
    And  and the authorization server authenticates the resource owner
    When the resource owner grants access to the client
    Then the resource owner is redirected to the client
    And the client receives the access token as a uri fragment