package uk.co.pragmaticdevelopers.gate.flow.authorizationcode

import uk.co.pragmaticdevelopers.gate.AccessTokenRequest
import uk.co.pragmaticdevelopers.gate.Credentials
import uk.co.pragmaticdevelopers.gate.Security
import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import uk.co.pragmaticdevelopers.gate.Required

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
