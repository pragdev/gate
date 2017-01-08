package uk.co.pragmaticdevelopers.gate

import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Subject
import uk.co.pragmaticdevelopers.gate.factory.GrantRequestFactory
import uk.co.pragmaticdevelopers.gate.httpconverter.GrantConverter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

class AuthorizationControllerSpec extends Specification {

    @Subject
    AuthorizationController authorizationController = new AuthorizationController()
    def response = Mock HttpServletResponse
    def request = Mock HttpServletRequest
    StringWriter responseBody

    def setup() {
        authorizationController.security = Mock Security
        authorizationController.converter = new GrantConverter(grantRequestFactory: new GrantRequestFactory())
        request.getQueryString() >> 'response_type=token&client_id=myid'

        responseBody = new StringWriter()
        def writer = new PrintWriter(responseBody)
        response = Mock(HttpServletResponse) {
            getWriter() >> writer
        }
    }

    def "an access request is considered a bad request when the query string does not contain response_type or client_id"() {
        given:
        request = Mock(HttpServletRequest) {
            getQueryString() >> queryString
        }

        when:
        authorizationController.doGet(request, response)

        then:
        1 * response.setStatus(SC_BAD_REQUEST)

        where:
        queryString << ['response_type=token', '', 'client_id=myid', 'x=1', 'x']
    }

    def "should deny an access request when the client cannot be identified"() {
        given:
        authorizationController.security.issueAccessRequest(*_) >> { throw new EntityNotFound() }

        when:
        authorizationController.doGet(request, response)

        then:
        1 * response.setStatus(SC_UNAUTHORIZED)
    }

    def "should deny an access request when the resource owner cannot be identified"() {
        given:
        request.getHeader(_) >> authHeader
        authorizationController.security.issueAccessRequest(*_) >> { throw new InvalidCredentialsException(new Credentials()) }

        when:
        authorizationController.doGet(request, response)

        then:
        1 * response.setStatus(SC_UNAUTHORIZED)

        where:
        authHeader << ["Basic ${"user:pass".bytes.encodeBase64().toString()}", null]
    }

    def "should respond with a new access request when client and resource owner are correctly identified"() {
        given:
        this.request.getHeader(_) >> "Basic ${"user:pass".bytes.encodeBase64().toString()}"
        def client = new Client(name: 'client')
        def owner = new ResourceOwner(displayName: 'owner')
        def accessRequest = new ImplicitFlowAccessRequest(id: "myid", client: client, resourceOwner: owner)
        authorizationController.security.issueAccessRequest(*_) >> accessRequest

        when:
        authorizationController.doGet(this.request, response)

        then:
        1 * response.setStatus(SC_OK)
        def json = parse responseBody.toString()
        json.id ==~ accessRequest.id
        json.status == 'PENDING'
    }

    def parse(json) {
        new JsonSlurper().parseText(json)
    }
}
