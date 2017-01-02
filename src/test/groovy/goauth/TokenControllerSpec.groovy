package goauth

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static goauth.AuthenticationFlow.PASSWORD
import static goauth.AuthenticationFlow.REFRESH_TOKEN
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

class TokenControllerSpec extends Specification {
    TokenController controller
    HttpServletResponse response
    private responseBody
    private request

    def setup() {
        responseBody = new StringWriter()
        def writer = new PrintWriter(responseBody)

        def security = Mock(Security) {
            authenticateResourceOwner(_ as Credentials) >> new AccessToken()
            issueAccessToken(_, _) >> new AccessToken()
        }

        response = Mock(HttpServletResponse) {
            getWriter() >> writer
        }

        request = GroovyMock(HttpServletRequest) {
            getParameter('grant_type') >> PASSWORD
            extractCredentialsFromBody() >> new Credentials(username: 'test', password: 'secret')
        }
        controller = new TokenController(security: security, presenter: new Presenter(), converter: new AccessTokenRequestConverter(factory: new AccessTokenRequestFactory()))
    }

    def "the new token response should be not cacheable"() {
        when:
        controller.doPost(request, response)

        then:
        1 * response.setHeader("Cache-Control", "no-store")
        1 * response.setHeader("Pragma", "no-cache")

        where:
        grantType << AuthenticationFlow.values()*.toString()
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

    def prettyJson(String json) {
        parse(json).toString()
    }

    def parse(json) {
        new JsonSlurper().parseText(json)
    }

}
