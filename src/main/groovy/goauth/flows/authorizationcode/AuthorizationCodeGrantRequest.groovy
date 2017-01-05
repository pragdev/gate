package goauth.flows.authorizationcode

import goauth.flows.authorizationcode.AuthorizationCodeAccessRequest
import goauth.GrantRequest

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
