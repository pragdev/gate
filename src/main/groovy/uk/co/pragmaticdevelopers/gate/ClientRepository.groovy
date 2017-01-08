package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class ClientRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    Client store(Client client) {
        if (!client.id) throw new IllegalArgumentException('A client id is required')

        Entity entity = Entity.make(client.id, Client)
        entity['id'] = client.id
        entity['secret'] = client.secret
        entity['name'] = client.name
        entity['type'] = client.type.toString()
        entity['redirectionUri'] = client.redirectionUri.toString()

        datastore.put entity
        client
    }

    boolean exists(String id) {
        findClientBy id
    }

    Client findBy(String id) {
        def entity = findClientBy id
        if (!entity) return null

        new Client(
                id: entity['id'],
                secret: entity['secret'],
                name: entity['name'],
                redirectionUri: new URI(entity['redirectionUri']),
                type: entity['type'],
        )
    }

    private Entity findClientBy(String id) {
        def query = datastore.prepare new Query(Client.simpleName).setFilter(new Query.FilterPredicate('id', IN, [id]))
        query.asSingleEntity()
    }
}
