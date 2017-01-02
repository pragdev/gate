package goauth

class AccessTokenAuthorizationCodeFlowRequest {
    @Required String grantType

    // TODO make it required if grant type = authorization_code
    String authorizationCode
    URI redirectUri
}
