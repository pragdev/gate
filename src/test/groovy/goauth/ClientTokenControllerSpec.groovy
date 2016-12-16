package goauth

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static goauth.AuthenticationFlow.CLIENT_CREDENTIALS
import static goauth.AuthenticationFlow.REFRESH_TOKEN

class ClientTokenControllerSpec extends Specification {
    ClientTokenController controller
    HttpServletResponse response
    HttpServletRequest request
    private responseBody

    def setup() {
        responseBody = new StringWriter()
        def writer = new PrintWriter(responseBody)
        response = Mock(HttpServletResponse) {
            getWriter() >> writer
        }

        def security = Mock(Security) {
            authenticateClient(_ as Credentials) >> new AccessToken()
        }
        request = GroovyMock(HttpServletRequest) {
            getParameter('grant_type') >> CLIENT_CREDENTIALS
            extractCredentialsFromHeader() >> new Credentials(username: 'test', password: 'secret')
        }

        controller = new ClientTokenController(security: security, presenter: new Presenter())
    }

    def "the new token response should be not cacheable"() {
        when:
        controller.doPost(request, response)

        then:
        1 * response.setHeader("Cache-Control", "no-store")
        1 * response.setHeader("Pragma", "no-cache")
    }

    def "the new token response should be in a JSON format"() {
        when:
        controller.doPost(request, response)

        then:
        1 * response.setContentType("application/json")

        where:
        grantType << AuthenticationFlow.values()*.toString()
    }

    def "should generate a new token when credentials are valid"() {
        when:
        controller.doPost(request, response)

        then:
        def json = parse responseBody.toString()
        with(json) {
            access_token != null
            !access_token.isEmpty()
            token_type == 'bearer'
            expires_in == 3600
        }

        where:
        grantType << AuthenticationFlow.values()*.toString()
    }

    def 'should respond with bad request if the token type is not client_credentials'() {
        given:
        def request = Mock(HttpServletRequest) { getParameter('grant_type') >> REFRESH_TOKEN }

        when:
        controller.doPost(request, response)

        then:
        1 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
    }

    String prettyJson(String json) {
        parse(json).toString()
    }

    Map parse(json) {
        new JsonSlurper().parseText(json)
    }

}
