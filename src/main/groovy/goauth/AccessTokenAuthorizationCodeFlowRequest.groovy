package goauth

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@TupleConstructor
@Canonical
class AccessTokenAuthorizationCodeFlowRequest extends AccessTokenRequest {
    @Required String authorizationCode
    URI redirectUri

    @Override
    protected void type() {
        grantType = 'authorization_code'
    }

    @Override
    boolean isValidType() {
        grantType == 'authorization_code'
    }

    @Override
    void authenticate(Credentials credentials, Security security) {
        security.authenticateClient credentials
    }
}
