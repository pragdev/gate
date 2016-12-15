package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class AccessRequestRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService
    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository

    AccessRequest store(AccessRequest accessRequest) {
        if (!accessRequest.id) throw new IllegalArgumentException('An accessRequest id is required')

        Entity entity = new Entity(asKey(accessRequest.id, 'AccessRequest'))
        entity.setProperty('id', accessRequest.id)
        entity.setProperty('status', accessRequest.status.name())
        entity.setProperty('client', accessRequest.client?.id)
        entity.setProperty('resourceOwner', accessRequest.resourceOwner?.username)

        datastore.put entity
        accessRequest
    }

    private Key asKey(String param, String kind) {
        def id = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, id)
    }

    boolean exists(String id) {
        findAccessRequestBy id
    }

    AccessRequest findBy(String id) {
        def entity = findAccessRequestBy id
        if (!entity) return null

        def client = entity.getProperty('client').toString()
        def name = entity.getProperty('resourceOwner').toString()
        def resourceOwner = resourceOwnerRepository.findBy name

        new AccessRequest(
                id: entity.getProperty('id').toString(),
                status: entity.getProperty('status').toString(),
                client: clientsRepository.findBy(client),
                resourceOwner: resourceOwner
        )
    }

    private Entity findAccessRequestBy(String id) {
        def query = datastore.prepare new Query(AccessRequest.simpleName).setFilter(new Query.FilterPredicate('id', IN, [id]))
        query.asSingleEntity() ?: null
    }

}
