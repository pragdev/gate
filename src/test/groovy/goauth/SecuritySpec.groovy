package goauth

import spock.lang.Specification

class SecuritySpec extends Specification {
  private Security security


  def "should provide a new Access Token when credentials are authenticated"() {
    given:
    CredentialsRepository repository = Mock() {
      1 * exists('antonio') >> true
      1 * find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      1 * store(_ as AccessToken) >> { args -> args.first() }
    }
    security = new Security(credentialsRepository: repository)

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
    security = new Security(credentialsRepository: repository)

    def credentials = new Credentials(username: 'wrongusername', password: 'test')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }

  def "should throw an invalid credential exception when the password does not match the stored password"() {
    given:
    CredentialsRepository repository = Mock() {
      exists('antonio') >> true
      find('antonio') >> new Credentials(username: 'antonio', password: 'test')
      0 * store(_ as AccessToken)
    }
    security = new Security(credentialsRepository: repository)

    def credentials = new Credentials(username: 'antonio', password: 'wrong')

    when:
    security.authenticate credentials

    then:
    thrown InvalidCredentialsException
  }
}
