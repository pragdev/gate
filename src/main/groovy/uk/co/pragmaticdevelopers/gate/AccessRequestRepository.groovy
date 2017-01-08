package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query
import groovy.transform.TupleConstructor
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class AccessRequestRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService
    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestFactory accessRequestFactory

    AccessRequest store(AccessRequest accessRequest) {
        if (!accessRequest.id) throw new IllegalArgumentException('An accessRequest id is required')

        Entity entity = Entity.make(accessRequest.id, AccessRequest)
        entity['id'] = accessRequest.id
        entity['status'] = accessRequest.status.name()
        entity['client'] = accessRequest.client?.id
        entity['resourceOwner'] = accessRequest.resourceOwner?.username
        entity['type'] = accessRequest.class.simpleName

        datastore.put entity
        accessRequest
    }

    boolean exists(String id) {
        findAccessRequestBy id
    }

    AccessRequest findBy(String id) {
        def entity = findAccessRequestBy id
        if (!entity) return null

        def clientId = entity['client'].toString()
        def name = entity['resourceOwner'].toString()
        def resourceOwner = resourceOwnerRepository.findBy name
        def client = clientsRepository.findBy clientId

        accessRequestFactory.make(entity, client, resourceOwner)
    }

    private Entity findAccessRequestBy(String id) {
        def query = datastore.prepare new Query(AccessRequest.simpleName).setFilter(new Query.FilterPredicate('id', IN, [id]))
        query.asSingleEntity()
    }

}
