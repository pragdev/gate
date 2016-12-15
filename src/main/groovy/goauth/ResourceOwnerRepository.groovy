package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class ResourceOwnerRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    ResourceOwner store(ResourceOwner resourceOwner) {
        Entity entity = new Entity(asKey(resourceOwner.username, 'ResourceOwner'))
        entity.setProperty('username', resourceOwner.username)
        entity.setProperty('password', resourceOwner.password)
        entity.setProperty('displayName', resourceOwner.displayName)

        datastore.put entity
        resourceOwner
    }

    private Key asKey(String param, String kind) {
        def username = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, username)
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
        Entity entity = new Entity(asKey(accessToken.value, 'AccessToken'))
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
