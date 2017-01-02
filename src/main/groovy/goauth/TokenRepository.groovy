package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity

class TokenRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    AccessToken store(AccessToken accessToken) {
        Entity entity = Entity.make(accessToken.value, AccessToken)
        entity['value'] = accessToken.value
        entity['issuedOn'] = accessToken.issuedOn.time
        entity['expiresIn'] = accessToken.expiresIn

        datastore.put entity
        accessToken
    }
}
