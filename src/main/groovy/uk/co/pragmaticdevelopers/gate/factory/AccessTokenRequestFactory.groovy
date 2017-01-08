package uk.co.pragmaticdevelopers.gate.factory

import uk.co.pragmaticdevelopers.gate.AuthenticationFlow
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AccessTokenAuthorizationCodeFlowRequest
import uk.co.pragmaticdevelopers.gate.flow.clientcredentials.AccessTokenClientCredentialsFlowRequest
import uk.co.pragmaticdevelopers.gate.flow.resourceownerpasswordcredentials.AccessTokenPasswordFlowRequest

import static uk.co.pragmaticdevelopers.gate.AuthenticationFlow.*

class AccessTokenRequestFactory {

    def make(Map args) {
        switch((AuthenticationFlow) args.grantType?.toUpperCase()) {
            case PASSWORD:
                return makeAccessTokenPasswordFlowRequest(args)
            case AUTHORIZATION_CODE:
                return makeAccessTokenAuthorizationCodeFlowRequest(args)
            case REFRESH_TOKEN:
                // TODO makeRefreshToken
                return null
            case CLIENT_CREDENTIALS:
                return makeAccessTokenClientCredentialsFlowRequest(args)
            default:
                throw new IllegalArgumentException()
        }
    }

    AccessTokenClientCredentialsFlowRequest makeAccessTokenClientCredentialsFlowRequest(Map args) {
        new AccessTokenClientCredentialsFlowRequest(grantType: args.grantType)
    }

    AccessTokenAuthorizationCodeFlowRequest makeAccessTokenAuthorizationCodeFlowRequest(Map args) {
        new AccessTokenAuthorizationCodeFlowRequest(grantType: args.grantType, redirectUri: args.redirectUri, authorizationCode: args.authorizationCode)
    }

    AccessTokenPasswordFlowRequest makeAccessTokenPasswordFlowRequest(Map args) {
        new AccessTokenPasswordFlowRequest(grantType: args.grantType)
    }
}
