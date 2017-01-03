package goauth

import groovy.transform.Canonical

@Canonical
class AuthorizationCodeAccessRequest extends AccessRequest {

    @Override
    def makeToken() {
        return new AuthorizationCode()
    }
}