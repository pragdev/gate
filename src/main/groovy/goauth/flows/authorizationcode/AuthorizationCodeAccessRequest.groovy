package goauth.flows.authorizationcode

import goauth.AccessRequest
import goauth.AuthorizationCode
import groovy.transform.Canonical

@Canonical
class AuthorizationCodeAccessRequest extends AccessRequest {

    @Override
    def makeToken() {
        return new AuthorizationCode()
    }
}