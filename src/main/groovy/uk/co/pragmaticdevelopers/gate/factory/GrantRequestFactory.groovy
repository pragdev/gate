package uk.co.pragmaticdevelopers.gate.factory

import uk.co.pragmaticdevelopers.gate.InvalidResponseTypeException
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeGrantRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitGrantRequest

class GrantRequestFactory {

    def make(Map args) {
        switch (args.responseType) {
            case 'token':
                makeImplicitGrantRequest(args)
                break;
            case 'code':
                makeAuthorizationCodeGrantRequest(args)
                break;
            default:
                throw new InvalidResponseTypeException()
        }
    }

    AuthorizationCodeGrantRequest makeAuthorizationCodeGrantRequest(Map args) {
        new AuthorizationCodeGrantRequest(args)
    }

    ImplicitGrantRequest makeImplicitGrantRequest(Map args) {
        new ImplicitGrantRequest(args)
    }
}
