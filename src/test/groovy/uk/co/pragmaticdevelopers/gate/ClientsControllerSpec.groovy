package uk.co.pragmaticdevelopers.gate

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ClientsControllerSpec extends Specification {

    ClientsController controller
    HttpServletResponse response
    StringWriter responseBody

    def setup() {
        controller = new ClientsController(security: Mock(Security))

        responseBody = new StringWriter()
        def writer = new PrintWriter(responseBody)
        response = Mock(HttpServletResponse) {
            getWriter() >> writer
        }
    }


    def prettyJson(String json) {
        parse(json).toString()
    }

    def parse(json) {
        new JsonSlurper().parseText(json)
    }

    def "should provide id and secret when a new client is created"() {
        given:
        String requestBody = """
    {
      "name": "my app",
      "type": "confidential",
      "redirectionUri": "http://mywebsite.com/grabtoken"
    }
    """

        HttpServletRequest request = Mock() {
            getInputStream() >> new ServletInputStreamMock(requestBody)
        }
        controller.security.register(_ as Client) >> { args ->
            def client = args.first()
            client.id = 'skjd-9akw9'
            client.secret = '3898fj02-29'
            return client
        }

        when:
        controller.doPost(request, response)

        then:
        def json = parse responseBody.toString()
        json.id ==~ /[A-z0-9-]+/
        json.secret ==~ /[A-z0-9-]+/
    }

    def "the new client response should be in a JSON format"() {
        given:
        String requestBody = """
    {
      "name": "my app",
      "type": "confidential",
      "redirectionUri": "http://mywebsite.com/grabtoken"
    }
    """

        HttpServletRequest request = Mock() {
            getInputStream() >> new ServletInputStreamMock(requestBody)
        }
        controller.security.register(_ as Client) >> { args ->
            def client = args.first()
            client.id = 'skjd-9akw9'
            client.secret = '3898fj02-29'
            return client
        }

        when:
        controller.doPost(request, response)

        then:
        1 * response.setContentType("application/json")
    }
}
