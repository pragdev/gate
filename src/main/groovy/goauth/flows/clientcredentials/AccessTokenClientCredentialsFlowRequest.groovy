package goauth.flows.clientcredentials

import goauth.AccessTokenRequest
import goauth.Credentials
import goauth.Security
import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@TupleConstructor
@Canonical
class AccessTokenClientCredentialsFlowRequest extends AccessTokenRequest {
    @Override
    protected void type() {
        grantType = 'client_credentials'
    }

    @Override
    boolean isValidType() {
        grantType == 'client_credentials'
    }

    @Override
    void authenticate(Credentials credentials, Security security) {
        security.authenticateClient credentials
    }
}
