package uk.co.pragmaticdevelopers.gate.flow.authorizationcode

import groovy.transform.Canonical
import uk.co.pragmaticdevelopers.gate.AccessRequest
import uk.co.pragmaticdevelopers.gate.AuthorizationCode
import uk.co.pragmaticdevelopers.gate.Token

@Canonical
class AuthorizationCodeAccessRequest extends AccessRequest {

    @Override
    Token generateToken() {
        return new AuthorizationCode()
    }
}