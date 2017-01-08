package uk.co.pragmaticdevelopers.gate.flow.implicit

import uk.co.pragmaticdevelopers.gate.AccessRequest
import uk.co.pragmaticdevelopers.gate.GrantRequest

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
