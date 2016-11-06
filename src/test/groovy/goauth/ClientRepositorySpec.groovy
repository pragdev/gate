package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import spock.lang.Specification
import spock.lang.Subject

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import static goauth.Client.Type.CONFIDENTIAL

class ClientRepositorySpec extends Specification {

  @Subject ClientRepository repository
  LocalServiceTestHelper helper
  DatastoreService datastoreService
  Client client

  def setup() {
    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    datastoreService = DatastoreServiceFactory.datastoreService
    repository = new ClientRepository(datastore: this.datastoreService)
    client = new Client(id: 'randomid', secret: 'randomsecret', name: 'myapp', redirectionUri: new URI('http://myapp.com/grabtoken'), type: CONFIDENTIAL)

    helper.setUp();
  }

  def cleanup() {
    helper.tearDown();
  }

  def "should store a client in the datastore"() {
    when:
    repository.store this.client

    then:
    datastoreService.prepare(new Query(Client.simpleName)).countEntities(withLimit(10)) == 1
  }

  def "should tell if a client exists in the datastore"() {
    given:
    repository.store client

    expect:
    repository.exists client.id
    !repository.exists('wrongid')
  }

  def "should find a client already stored in the datastore"() {
    given:
    repository.store client

    when:
    def storedCredentials = repository.find client.id

    then:
    storedCredentials == client
  }

  def "should respond with null when cannot find credentials in the datastore"() {
    expect:
    repository.find('wrong') == null
  }

}


