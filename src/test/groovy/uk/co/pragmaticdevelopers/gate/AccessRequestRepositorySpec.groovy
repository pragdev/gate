package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.EmbeddedEntity
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeAccessRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import static uk.co.pragmaticdevelopers.gate.Client.Type.CONFIDENTIAL

class AccessRequestRepositorySpec extends Specification {

    @Subject
    AccessRequestRepository repository
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

    @Unroll
    def 'should store an #type.simpleName'() {
        AccessRequest accessRequest = type.newInstance(client: client, resourceOwner: resourceOwner)

        when:
        repository.store accessRequest

        then:
        def preparedQuery = datastoreService.prepare(new Query(AccessRequest.simpleName))
        preparedQuery.countEntities(withLimit(10)) == 1
        def entity = preparedQuery.asSingleEntity()
        entity['type'] == type.simpleName

        where:
        type << [ImplicitFlowAccessRequest, AuthorizationCodeAccessRequest]
    }

    @Ignore
    @Unroll
    def 'should store an #type.simpleName with a token'() {
        AccessRequest accessRequest = type.newInstance(client: client, resourceOwner: resourceOwner, token: token)

        when:
        repository.store accessRequest

        then:
        def preparedQuery = datastoreService.prepare(new Query(AccessRequest.simpleName))
        preparedQuery.countEntities(withLimit(10)) == 1
        def entity = preparedQuery.asSingleEntity()
        entity['type'] == type.simpleName
        entity['token'] in EmbeddedEntity
        EmbeddedEntity tokenEntity = entity['token']
        tokenEntity.getProperty('value') =~ /[A-z09-]+/

        where:
        type                           | token
        ImplicitFlowAccessRequest      | new AccessToken()
        AuthorizationCodeAccessRequest | new AuthorizationCode()
    }

    @Unroll
    def 'should find an #type.simpleName'() {
        given:
        AccessRequest accessRequest = type.newInstance(client: client, resourceOwner: resourceOwner)
        repository.store accessRequest

        when:
        AccessRequest storedAccessRequest = repository.findBy accessRequest.id

        then:
        storedAccessRequest == accessRequest

        where:
        type << [ImplicitFlowAccessRequest, AuthorizationCodeAccessRequest]
    }

}
