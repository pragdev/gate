package uk.co.pragmaticdevelopers.gate.flow.implicit

import uk.co.pragmaticdevelopers.gate.AccessRequest
import groovy.transform.Canonical
import uk.co.pragmaticdevelopers.gate.AccessToken

@Canonical
class ImplicitFlowAccessRequest extends AccessRequest {

    @Override
    AccessToken makeToken() {
        new AccessToken()
    }
}