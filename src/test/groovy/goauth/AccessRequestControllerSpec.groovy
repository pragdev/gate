package goauth

import groovy.json.JsonBuilder
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static goauth.AccessRequest.Status.GRANTED
import static goauth.AccessRequest.Status.PENDING
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_NOT_FOUND

class AccessRequestControllerSpec extends Specification {

    @Subject
    def accessRequestController = new AccessRequestController()

    def request = Mock HttpServletRequest
    def response = Mock HttpServletResponse
    def security = Mock Security
    private builder = new JsonBuilder()

    def setup() {
        accessRequestController.security = security
    }

    def "should respond with not found when the access request id is not in the repository"() {
        given:
        def builder = new JsonBuilder()
        builder {
            id "wrongid"
            status GRANTED.toString()
        }
        1 * request.getInputStream() >> new ServletInputStreamMock(builder.toString())
        1 * security.grantAccess(_) >> { throw new EntityNotFound() }

        when:
        accessRequestController.doPut(request, response)

        then:
        1 * response.setStatus(SC_NOT_FOUND)
    }

    def "should respond with bad request when the access request status is not GRANTED or DENIED"() {
        given:
        def builder = new JsonBuilder()
        builder {
            id "avalidid"
            status "wrong"
        }
        1 * request.getInputStream() >> new ServletInputStreamMock(builder.toString())

        when:
        accessRequestController.doPut(request, response)

        then:
        1 * response.setStatus(SC_BAD_REQUEST)
    }

    def "should respond with bad request when security throws an invalid status exception"() {
        given:
        def builder = new JsonBuilder()
        builder {
            id "avalidid"
            status PENDING.toString()
        }
        1 * request.getInputStream() >> new ServletInputStreamMock(builder.toString())
        security.grantAccess(_) >> { throw new InvalidStatusException() }

        when:
        accessRequestController.doPut(request, response)

        then:
        1 * response.setStatus(SC_BAD_REQUEST)
    }

    def "should update the access request and redirect the client to it's redirection uri and pass the access token as a uri fragment"() {
        given:
        def token = new AccessToken()
        def redirectionUri = new URI('http://test.com/callback')
        1 * security.grantAccess(_) >> token
        1 * security.redirectUriFor(_ as AccessRequest) >> redirectionUri
        builder {
            id "validid"
            status GRANTED.toString()
        }
        1 * request.getInputStream() >> new ServletInputStreamMock(this.builder.toString())

        when:
        accessRequestController.doPut(request, response)

        then:
        1 * response.setStatus(SC_MOVED_TEMPORARILY)
        1 * response.setHeader('Location', {
            it == "$redirectionUri#access_token=${token.value}&token_type=example&expires_in=${token.expiresIn}"
        })
    }

}
