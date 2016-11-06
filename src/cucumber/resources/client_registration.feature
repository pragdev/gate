Feature: Before initiating the protocol, the client registers with the
  authorization server.  The means through which the client registers
  with the authorization server are beyond the scope of the OAUTH
  specification but typically involve end-user interaction with an HTML
  registration form.

  Client registration does not require a direct interaction between the
  client and the authorization server.  When supported by the
  authorization server, registration can rely on other means for
  establishing trust and obtaining the required client properties
  (e.g., redirection URI, client type).  For example, registration can
  be accomplished using a self-issued or third-party-issued assertion,
  or by the authorization server performing client discovery using a
  trusted channel.

  When registering a client, the client developer SHALL:
  o  specify the client type as described in Section 2.1,
  o  provide its client redirection URIs as described in Section 3.1.2, and
  o  include any other information required by the authorization server
  (e.g., application name, website, description, logo image, the
  acceptance of legal terms).



  Scenario: A client is registered on the platform
    Given a authentication server administrator has already obtained an access token
    When the admin makes a "POST" request to the Authorization Server at the path "/clients" with body
    """
    {
      "name": "my app",
      "type": "confidential",
      "redirectionUri": "http://mywebsite.com/grabtoken"
    }
    """
    Then the authorization server response should be
    """
    {
      "id": "__match_regex:[A-z0-9-]__",
      "secret": "__match_regex:[A-z0-9-]__"
    }
    """
