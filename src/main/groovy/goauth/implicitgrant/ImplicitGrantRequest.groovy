package goauth.implicitgrant

import goauth.AccessRequest
import goauth.AuthorizationCodeAccessRequest
import goauth.GrantRequest
import goauth.ImplicitFlowAccessRequest

class ImplicitGrantRequest extends GrantRequest {

    boolean isValidType() {
        responseType == 'token'
    }

    @Override
    protected void type() {
        this.responseType = 'token'
    }

    @Override
    protected AccessRequest makeAccessRequest(Map args) {
        return new ImplicitFlowAccessRequest(client: args.client, resourceOwner: args.resourceOwner)
    }
}
