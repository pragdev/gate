package goauth

import spock.lang.Specification

import static goauth.Client.Type.CONFIDENTIAL

class SecuritySpec extends Specification {
  private Security security

  def setup() {
    security = new Security()
  }

  def "should provide a new Access Token when user credentials are authenticated"() {
    given:
    security.credentialsRepository = Mock(CredentialsRepository) {
      1 * exists('antonio') >> true
      1 * find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      1 * store(_ as AccessToken) >> { args -> args.first() }
    }

    def credentials = new Credentials(username: 'antonio', password: 'test')

    when:
    AccessToken accessToken = security.authenticate credentials

    then:
    accessToken
  }

  def "should throw an invalid credential exception when the username is not present in the repository"() {
    given:

    security.credentialsRepository = Mock(CredentialsRepository) {
      exists('wrongusername') >> false
      0 * store(_ as AccessToken)
    }

    def credentials = new Credentials(username: 'wrongusername', password: 'test')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }

  def "should throw an invalid credential exception when the user password does not match the stored password"() {
    given:

    security.credentialsRepository = Mock(CredentialsRepository) {
      exists('antonio') >> true
      find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      0 * store(_ as AccessToken)
    }

    def credentials = new Credentials(username: 'antonio', password: 'wrong')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }

  def "should provide a new Access Token when client credentials are authenticated"() {
    given:

    security.credentialsRepository = Mock(CredentialsRepository) {
      1 * exists('myapp') >> true
      1 * find('myapp') >> new Credentials(username: 'myapp', password: 'test')
      1 * store(_ as AccessToken) >> { args -> args.first() }
    }
    def credentials = new Credentials(username: 'myapp', password: 'test')

    when:
    AccessToken accessToken = security.authenticate credentials

    then:
    accessToken
  }

  def "should throw an invalid credential exception when the client name is not present in the repository"() {
    given:

    security.credentialsRepository = Mock(CredentialsRepository) {
      exists('wrongclientname') >> false
      0 * store(_ as AccessToken)
    }

    when:
    security.authenticate new Credentials(username: 'wrongclientname', password: 'test')

    then:
    thrown InvalidCredentialsException
  }

  def "should throw an invalid credential exception when the client password does not match the stored password"() {
    given:

    security.credentialsRepository = Mock(CredentialsRepository) {
      exists('myapp') >> true
      find('myapp') >> new Credentials(username: 'myapp', password: 'test')
      0 * store(_ as AccessToken)
    }

    def credentials = new Credentials(username: 'myapp', password: 'wrong')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }

  def "should register a new client on the system"() {
    given:
    def client = new Client(name: 'myapp', redirectionUri: new URI('http://myapp.com/grabtoken'), type: CONFIDENTIAL)
    security.clientsRepository = GroovyMock(ClientRepository) {
      store(client) >> client
    }

    when:
    security.register client

    then:
    client.id ==~ /[A-z0-9-]+/
    client.secret ==~ /[A-z0-9-]+/

    1 * security.clientsRepository.store(client)
  }
}
