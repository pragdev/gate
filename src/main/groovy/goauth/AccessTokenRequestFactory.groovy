package goauth

import static goauth.AuthenticationFlow.AUTHORIZATION_CODE
import static goauth.AuthenticationFlow.PASSWORD
import static goauth.AuthenticationFlow.REFRESH_TOKEN

class AccessTokenRequestFactory {

    def make(Map args) {
        switch((AuthenticationFlow) args.grantType?.toUpperCase()) {
            case PASSWORD:
                return makeAccessTokenPasswordFlowRequest(args)
            case AUTHORIZATION_CODE:
                return makeAccessTokenAuthorizationCodeFlowRequest(args)
            case REFRESH_TOKEN:
                // TODO makeRefreshToken
                return makeAccessTokenPasswordFlowRequest(args)
            default:
                throw new IllegalArgumentException()
        }
    }

    AccessTokenAuthorizationCodeFlowRequest makeAccessTokenAuthorizationCodeFlowRequest(Map args) {
        new AccessTokenAuthorizationCodeFlowRequest(grantType: args.grantType, redirectUri: args.redirectUri, authorizationCode: args.authorizationCode)
    }

    AccessTokenPasswordFlowRequest makeAccessTokenPasswordFlowRequest(Map args) {
        new AccessTokenPasswordFlowRequest(grantType: args.grantType)
    }
}
