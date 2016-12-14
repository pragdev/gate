package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class ClientRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    Client store(Client client) {
        if(!client.id) throw new IllegalArgumentException('A client id is required')

        Entity entity = new Entity(asKey(client.id, 'Client'))
        entity.setProperty('id', client.id)
        entity.setProperty('secret', client.secret)
        entity.setProperty('name', client.name)
        entity.setProperty('type', client.type.toString())
        entity.setProperty('redirectionUri', client.redirectionUri.toString())

        datastore.put entity
        client
    }

    private Key asKey(String param, String kind) {
        def id = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, id)
    }

    boolean exists(String id) {
        findClientBy id
    }

    Client findBy(String id) {
        def entity = findClientBy id
        if (!entity) return null

        new Client(
                id: entity.getProperty('id').toString(),
                secret: entity.getProperty('secret').toString(),
                name: entity.getProperty('name').toString(),
                redirectionUri: new URI(entity.getProperty('redirectionUri').toString()),
                type: entity.getProperty('type').toString(),
        )
    }

    private Entity findClientBy(String id) {
        def query = datastore.prepare new Query(Client.simpleName).setFilter(new Query.FilterPredicate('id', IN, [id]))
        query.asSingleEntity() ?: null
    }
}
