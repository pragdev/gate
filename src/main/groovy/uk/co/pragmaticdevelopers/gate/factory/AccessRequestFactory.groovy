package uk.co.pragmaticdevelopers.gate.factory

import com.google.appengine.api.datastore.Entity
import uk.co.pragmaticdevelopers.gate.AccessRequest
import uk.co.pragmaticdevelopers.gate.Client
import uk.co.pragmaticdevelopers.gate.GrantRequest
import uk.co.pragmaticdevelopers.gate.ResourceOwner
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeAccessRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest

class AccessRequestFactory {
    AccessRequest make(Map args) {
        GrantRequest grantRequest = args.grantRequest
        grantRequest.makeAccessRequest(args)
    }

    AccessRequest make(Entity entity, Client client, ResourceOwner resourceOwner) {
        switch (entity['type']) {
            case 'ImplicitFlowAccessRequest':
                return new ImplicitFlowAccessRequest(
                        client: client,
                        resourceOwner: resourceOwner,
                        id: entity.getProperty('id').toString(),
                        status: entity.getProperty('status').toString()
                )
                break
            case 'AuthorizationCodeAccessRequest':
                return new AuthorizationCodeAccessRequest(
                        client: client,
                        resourceOwner: resourceOwner,
                        id: entity.getProperty('id').toString(),
                        status: entity.getProperty('status').toString()
                )
                break
            default:
                throw new IllegalArgumentException()
        }
    }
}
