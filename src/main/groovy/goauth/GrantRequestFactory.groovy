package goauth

import goauth.flows.authorizationcode.AuthorizationCodeGrantRequest
import goauth.implicitgrant.ImplicitGrantRequest

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
