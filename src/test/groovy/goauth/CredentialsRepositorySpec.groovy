package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import spock.lang.Specification
import spock.lang.Subject

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit

class CredentialsRepositorySpec extends Specification {

    @Subject
    def repository
    LocalServiceTestHelper helper
    DatastoreService datastoreService

    def setup() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        datastoreService = DatastoreServiceFactory.datastoreService
        repository = new CredentialsRepository(datastore: this.datastoreService)

        helper.setUp();
    }

    def cleanup() {
        helper.tearDown();
    }

    def "should store credentials in the datastore"() {
        given:
        def credentials = new Credentials(username: 'Antonio', password: 'mylittlesecret')

        when:
        repository.store credentials

        then:
        datastoreService.prepare(new Query(Credentials.simpleName)).countEntities(withLimit(10)) == 1
    }

    def "should tell if credentials exists in the datastore"() {
        given:
        def credentials = new Credentials(username: 'Antonio', password: 'mylittlesecret')
        repository.store credentials

        expect:
        repository.exists credentials.username
        !repository.exists('wrongusername')
    }

    def "should find credentials already stored in the datastore"() {
        given:
        def credentials = new Credentials(username: 'Antonio', password: 'mylittlesecret')
        repository.store credentials

        when:
        def storedCredentials = repository.findBy credentials.username

        then:
        storedCredentials == credentials
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


