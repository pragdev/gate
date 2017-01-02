package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import spock.lang.Specification
import spock.lang.Subject

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit

class TokenRepositorySpec extends Specification {

    @Subject TokenRepository repository

    LocalServiceTestHelper helper
    DatastoreService datastoreService

    def setup() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        datastoreService = DatastoreServiceFactory.datastoreService
        repository = new TokenRepository(datastore: this.datastoreService)

        helper.setUp();
    }

    def cleanup() {
        helper.tearDown();
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
