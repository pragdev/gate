package goauth

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenControllerTest extends Specification {
  TokenController controller
  HttpServletResponse response
  private responseBody

  def setup() {
    responseBody = new StringWriter()
    def writer = new PrintWriter(responseBody)

    controller = new TokenController(security: Mock(Security) {
      authenticate(_ as Credentials) >> new AccessToken()
    })
    response = Mock(HttpServletResponse) {
      getWriter() >> writer
    }
  }

  def "the new token response should be not cacheable"() {
    given:
    def request = Mock(HttpServletRequest) { getParameter('grant_type') >> grantType }

    def security = Mock(Security) {
      authenticate(*_) >> new AccessToken()
    }
    controller.security = security

    when:
    controller.doPost(request, response)

    then:
    1 * response.setHeader("Cache-Control", "no-store")
    1 * response.setHeader("Pragma", "no-cache")

    where:
    grantType << AuthenticationFlow.values()*.toString()
  }

  def "the new token response should be in a JSON format"() {
    given:
    def request = Mock(HttpServletRequest) { getParameter('grant_type') >> grantType }

    def security = Mock(Security) {
      authenticate(*_) >> new AccessToken()
    }
    controller.security = security

    when:
    controller.doPost(request, response)

    then:
    1 * response.setContentType("application/json")

    where:
    grantType << AuthenticationFlow.values()*.toString()
  }

  def "should generate a new token when credentials are valid"() {
    given:
    def request = Mock(HttpServletRequest) { getParameter('grant_type') >> grantType }

    def security = Mock(Security) {
      authenticate(*_) >> new AccessToken()
    }
    controller.security = security

    when:
    controller.doPost(request, response)

    then:
    def json = parse(responseBody.toString())
    json.access_token != null
    !json.access_token.isEmpty()
    json.token_type == 'bearer'
    json.expires_in == 3600

    where:
    grantType << AuthenticationFlow.values()*.toString()
  }

  def prettyJson(String json) {
    parse(json).toString()
  }

  def parse(json) {
    new JsonSlurper().parseText(json)
  }

  def "should extract credentials from the request body"() {
    given:

    HttpServletRequest request = Mock(HttpServletRequest) {
      getParameter('username') >> 'antonio'
      getParameter('password') >> 'test'
    }

    when:
    def credentials = controller.extractCredentialsFromBody request

    then:
    credentials == new Credentials('antonio', 'test')
  }

  def "should not extract any credentials from the request body if params are missing"() {
    given:

    HttpServletRequest request = Mock(HttpServletRequest) {
      getParameter('username') >> null
      getParameter('password') >> null
    }

    expect:
    !controller.extractCredentialsFromBody(request)
  }

  def "should extract credentials from the Authorization header"() {
    given:

    HttpServletRequest request = Mock(HttpServletRequest) {
      getHeader('Authorization') >> 'Basic bXlhcHA6dGVzdA=='
    }

    when:
    def credentials = controller.extractCredentialsFromHeader request

    then:
    credentials == new Credentials('myapp', 'test')
  }

  def "should not extract any credentials from the Authorization header if params are missing"() {
    given:

    HttpServletRequest request = Mock(HttpServletRequest) {
      getHeader('Authorization') >> header
    }

    expect:
    !controller.extractCredentialsFromHeader(request)

    where:
    header << ['', null, 'Basic wrong9hjformat', 'Basic ', 'wrong']
  }


}
