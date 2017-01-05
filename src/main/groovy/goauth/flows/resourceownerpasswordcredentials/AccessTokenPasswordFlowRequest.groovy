package goauth.flows.resourceownerpasswordcredentials

import goauth.AccessTokenRequest
import goauth.Credentials
import goauth.Security
import groovy.transform.Canonical

@Canonical
class AccessTokenPasswordFlowRequest extends AccessTokenRequest {
    @Override
    protected void type() {
        grantType = 'password'
    }

    @Override
    boolean isValidType() {
        grantType == 'password'
    }

    @Override
    void authenticate(Credentials credentials, Security security) {
        security.authenticateResourceOwner credentials
    }
}
