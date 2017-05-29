package uk.co.pragmaticdevelopers.gate.flow.resourceownerpasswordcredentials

import groovy.transform.Canonical
import uk.co.pragmaticdevelopers.gate.AccessTokenRequest
import uk.co.pragmaticdevelopers.gate.Credentials
import uk.co.pragmaticdevelopers.gate.Security

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
    def authenticate(Credentials credentials, Security security) {
        security.authenticateResourceOwner credentials
    }
}
