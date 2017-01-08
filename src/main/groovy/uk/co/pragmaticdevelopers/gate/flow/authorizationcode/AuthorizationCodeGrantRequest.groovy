package uk.co.pragmaticdevelopers.gate.flow.authorizationcode

import uk.co.pragmaticdevelopers.gate.GrantRequest

class AuthorizationCodeGrantRequest extends GrantRequest {

    boolean isValidType() {
        responseType == 'code'
    }

    @Override
    protected void type() {
        responseType = 'code'
    }

    @Override
    protected AuthorizationCodeAccessRequest makeAccessRequest(Map args) {
        return new AuthorizationCodeAccessRequest(client: args.client, resourceOwner: args.resourceOwner)
    }
}
