package uk.co.pragmaticdevelopers.gate

import spock.lang.Specification
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeAccessRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest

import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.GRANTED

class AccessRequestSpec extends Specification {

    AccessRequest accessRequest

    def 'should be granted'() {
        when:
        Token accessToken = accessRequest.grant()

        then:
        accessRequest.status == GRANTED
        accessToken

        where:
        accessRequest << [new AuthorizationCodeAccessRequest(), new ImplicitFlowAccessRequest()]
    }

}
