package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query
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

        new ResourceOwner(
                username: entity['username'],
                password: entity['password'],
                displayName: entity['displayName']
        )
    }

    private Entity findResourceOwnerBy(String username) {
        def query = datastore.prepare new Query(ResourceOwner.simpleName).setFilter(new Query.FilterPredicate('username', IN, [username]))
        query.asSingleEntity()
    }
}
