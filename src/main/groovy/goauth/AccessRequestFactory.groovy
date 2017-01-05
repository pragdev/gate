package goauth

import com.google.appengine.api.datastore.Entity
import goauth.flows.authorizationcode.AuthorizationCodeAccessRequest
import goauth.flows.implicit.ImplicitFlowAccessRequest

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
