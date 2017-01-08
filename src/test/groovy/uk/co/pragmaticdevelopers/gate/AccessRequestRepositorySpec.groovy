package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeAccessRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest
import spock.lang.Specification
import spock.lang.Subject
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import static uk.co.pragmaticdevelopers.gate.Client.Type.CONFIDENTIAL

class AccessRequestRepositorySpec extends Specification {

    @Subject AccessRequestRepository repository
    LocalServiceTestHelper helper
    DatastoreService datastoreService
    Client client
    ResourceOwner resourceOwner

    def setup() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        datastoreService = DatastoreServiceFactory.datastoreService
        client = new Client(id: 'randomid', secret: 'randomsecret', name: 'myapp', redirectionUri: new URI('http://myapp.com/grabtoken'), type: CONFIDENTIAL)
        resourceOwner = new ResourceOwner(username: 'user', password: 'secret')

        def resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            findBy('user') >> resourceOwner
        }
        def clientRepository = Mock(ClientRepository) {
            findBy('randomid') >> client
        }

        repository = new AccessRequestRepository(
                datastore: this.datastoreService,
                resourceOwnerRepository: resourceOwnerRepository,
                clientsRepository: clientRepository,
                accessRequestFactory: new AccessRequestFactory()
        )


        helper.setUp();
    }

    def cleanup() {
        helper.tearDown();
    }

    def 'should store an ImplicitFlowAccessRequest'() {
        given:
        def implicitFlowAccessRequest = new ImplicitFlowAccessRequest(client: client, resourceOwner: resourceOwner)

        when:
        repository.store implicitFlowAccessRequest

        then:
        def preparedQuery = datastoreService.prepare(new Query(AccessRequest.simpleName))
        preparedQuery.countEntities(withLimit(10)) == 1
        def entity = preparedQuery.asSingleEntity()
        entity['type'] == 'ImplicitFlowAccessRequest'
    }

    def 'should store an AuthorizationCodeAccessRequest'() {
        given:
        def authorizationCodeAccessRequest = new AuthorizationCodeAccessRequest(client: client, resourceOwner: resourceOwner)

        when:
        repository.store authorizationCodeAccessRequest

        then:
        def preparedQuery = datastoreService.prepare(new Query(AccessRequest.simpleName))
        preparedQuery.countEntities(withLimit(10)) == 1
        def entity = preparedQuery.asSingleEntity()
        entity['type'] == 'AuthorizationCodeAccessRequest'
    }

    def 'should find an ImplicitFlowAccessRequest'() {
        given:
        def implicitFlowAccessRequest = new ImplicitFlowAccessRequest(client: client, resourceOwner: resourceOwner)
        repository.store implicitFlowAccessRequest

        when:
        ImplicitFlowAccessRequest storedAccessRequest = repository.findBy implicitFlowAccessRequest.id

        then:
        storedAccessRequest == implicitFlowAccessRequest
    }

    def 'should find an AuthorizationCodeAccessRequest'() {
        given:
        def authorizationCodeAccessRequest = new AuthorizationCodeAccessRequest(client: client, resourceOwner: resourceOwner)
        repository.store authorizationCodeAccessRequest

        when:
        AuthorizationCodeAccessRequest storedAccessRequest = repository.findBy authorizationCodeAccessRequest.id

        then:
        storedAccessRequest == authorizationCodeAccessRequest
    }
}
