package uk.co.pragmaticdevelopers.gate.flow.clientcredentials

import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import uk.co.pragmaticdevelopers.gate.AccessTokenRequest
import uk.co.pragmaticdevelopers.gate.Credentials
import uk.co.pragmaticdevelopers.gate.Security

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
    def authenticate(Credentials credentials, Security security) {
        security.authenticateClient credentials
    }
}
