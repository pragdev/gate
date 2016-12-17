package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class ResourceOwnerRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    ResourceOwner store(ResourceOwner resourceOwner) {
        Entity entity = Entity.make(resourceOwner.username, ResourceOwner)
        entity['username'] = resourceOwner.username
        entity['password'] = resourceOwner.password
        entity['displayName'] = resourceOwner.displayName

        datastore.put entity
        resourceOwner
    }

    boolean exists(String username) {
        findResourceOwnerBy username
    }

    ResourceOwner findBy(String username) {
        def entity = findResourceOwnerBy username
        if (!entity) return null

        new ResourceOwner(entity.getProperty('username').toString(), entity.getProperty('password').toString(), entity.getProperty('displayName').toString())
    }

    AccessToken store(AccessToken accessToken) {
        Entity entity = Entity.make(accessToken.value, AccessToken)
        entity.setProperty('value', accessToken.value)
        entity.setProperty('issuedOn', accessToken.issuedOn.time)
        entity.setProperty('expiresIn', accessToken.expiresIn)

        datastore.put entity
        accessToken
    }

    private Entity findResourceOwnerBy(String username) {
        def query = datastore.prepare new Query(ResourceOwner.simpleName).setFilter(new Query.FilterPredicate('username', IN, [username]))
        query.asSingleEntity() ?: null
    }
}
