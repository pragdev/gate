package goauth.authorizationcodegrant

import goauth.AuthorizationCodeAccessRequest
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
