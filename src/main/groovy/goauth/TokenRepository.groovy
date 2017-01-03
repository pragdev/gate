package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity

class TokenRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    Token store(Token token) {
        Entity entity = Entity.make(token.value, Token)
        entity['value'] = token.value
        entity['issuedOn'] = token.issuedOn.time
        entity['expiresIn'] = token.expiresIn
        entity['type'] = token.class.simpleName

        datastore.put entity
        token
    }
}
