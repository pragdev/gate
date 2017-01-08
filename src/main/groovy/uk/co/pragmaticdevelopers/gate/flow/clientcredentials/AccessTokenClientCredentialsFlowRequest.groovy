package uk.co.pragmaticdevelopers.gate.flow.clientcredentials

import uk.co.pragmaticdevelopers.gate.AccessTokenRequest
import uk.co.pragmaticdevelopers.gate.Credentials
import uk.co.pragmaticdevelopers.gate.Security
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
