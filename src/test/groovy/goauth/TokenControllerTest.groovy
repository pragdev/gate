package goauth

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenControllerTest extends Specification {
  TokenController controller = new TokenController()
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
    when:
    controller.doPost(Mock(HttpServletRequest), response)

    then:
    1 * response.setHeader("Cache-Control", "no-store")
    1 * response.setHeader("Pragma", "no-cache")
  }

  def "the new token response should be in a JSON format"() {
    when:
    controller.doPost(Mock(HttpServletRequest), response)

    then:
    1 * response.setContentType("application/json")
  }

  def "should generate a new token when credentials are valid"() {
    when:
    controller.doPost(Mock(HttpServletRequest), response)

    then:
    def json = parse(responseBody.toString())
    json.access_token != null
    !json.access_token.isEmpty()
    json.token_type == 'bearer'
    json.expires_in == 3600
  }

  def prettyJson(String json) {
    parse(json).toString()
  }

  def parse(json) {
    new JsonSlurper().parseText(json)
  }

}
