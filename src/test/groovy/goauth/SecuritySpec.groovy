package goauth

import spock.lang.Specification

import static goauth.AuthenticationFlow.CLIENT_CREDENTIALS
import static goauth.AuthenticationFlow.PASSWORD

class SecuritySpec extends Specification {
  private Security security

  def "should provide a new Access Token when user credentials are authenticated"() {
    given:
    CredentialsRepository repository = Mock() {
      1 * exists('antonio') >> true
      1 * find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      1 * store(_ as AccessToken) >> { args -> args.first() }
    }
    security = new Security(repository)

    def credentials = new Credentials(username: 'antonio', password: 'test')

    when:
    AccessToken accessToken = security.authenticate credentials

    then:
    accessToken
  }

  def "should throw an invalid credential exception when the username is not present in the repository"() {
    given:
    CredentialsRepository repository = Mock() {
      exists('wrongusername') >> false
      0 * store(_ as AccessToken)
    }
    security = new Security(repository)

    def credentials = new Credentials(username: 'wrongusername', password: 'test')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }

  def "should throw an invalid credential exception when the user password does not match the stored password"() {
    given:
    CredentialsRepository repository = Mock() {
      exists('antonio') >> true
      find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      0 * store(_ as AccessToken)
    }
    security = new Security(repository)

    def credentials = new Credentials(username: 'antonio', password: 'wrong')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }




  def "should provide a new Access Token when client credentials are authenticated"() {
    given:
    CredentialsRepository repository = Mock() {
      1 * exists('myapp') >> true
      1 * find('myapp') >> new Credentials(username: 'myapp', password: 'test')
      1 * store(_ as AccessToken) >> { args -> args.first() }
    }
    security = new Security(repository)
    def credentials = new Credentials(username: 'myapp', password: 'test')

    when:
    AccessToken accessToken = security.authenticate credentials

    then:
    accessToken
  }

  def "should throw an invalid credential exception when the client name is not present in the repository"() {
    given:
    CredentialsRepository repository = Mock() {
      exists('wrongclientname') >> false
      0 * store(_ as AccessToken)
    }
    security = new Security(repository)

    when:
    security.authenticate new Credentials(username: 'wrongclientname', password: 'test')

    then:
    thrown InvalidCredentialsException
  }

  def "should throw an invalid credential exception when the client password does not match the stored password"() {
    given:
    CredentialsRepository repository = Mock() {
      exists('myapp') >> true
      find('myapp') >> new Credentials(username: 'myapp', password: 'test')
      0 * store(_ as AccessToken)
    }
    security = new Security(repository)

    def credentials = new Credentials(username: 'myapp', password: 'wrong')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }
}
