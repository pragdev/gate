package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import spock.lang.Specification
import spock.lang.Subject

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit

class ResourceOwnerRepositorySpec extends Specification {

    @Subject
    ResourceOwnerRepository repository
    LocalServiceTestHelper helper
    DatastoreService datastoreService

    def setup() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        datastoreService = DatastoreServiceFactory.datastoreService
        repository = new ResourceOwnerRepository(datastore: this.datastoreService)

        helper.setUp();
    }

    def cleanup() {
        helper.tearDown();
    }

    def "should store credentials in the datastore"() {
        given:
        def resourceOwner = new ResourceOwner(username: 'Antonio', password: 'mylittlesecret', displayName: 'Ayeye Brazorf')

        when:
        repository.store resourceOwner

        then:
        datastoreService.prepare(new Query(ResourceOwner.simpleName)).countEntities(withLimit(10)) == 1
    }

    def "should tell if credentials exists in the datastore"() {
        given:
        def resourceOwner = new ResourceOwner(username: 'Antonio', password: 'mylittlesecret', displayName: 'Ayeye Brazorf')
        repository.store resourceOwner

        expect:
        repository.exists resourceOwner.username
        !repository.exists('wrongusername')
    }

    def "should find credentials already stored in the datastore"() {
        given:
        def resourceOwner = new ResourceOwner(username: 'Antonio', password: 'mylittlesecret', displayName: 'Ayeye Brazorf')
        repository.store resourceOwner

        when:
        def storedCredentials = repository.findBy resourceOwner.username

        then:
        storedCredentials == resourceOwner
    }

    def "should respond with null when cannot find credentials in the datastore"() {
        expect:
        repository.findBy('wrong') == null
    }

    def "should store an access token in the datastore"() {
        given:
        def accessToken = new AccessToken()

        when:
        repository.store accessToken

        then:
        def numberOfTokens = datastoreService.prepare(new Query(AccessToken.simpleName)).countEntities(withLimit(10))
        numberOfTokens == 1
    }
}


