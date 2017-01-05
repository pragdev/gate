package goauth

import goauth.flows.authorizationcode.AccessTokenAuthorizationCodeFlowRequest
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

class AccessTokenRequestConverterSpec extends Specification {

    @Subject
    AccessTokenRequestConverter converter
    private HttpServletRequest request

    def setup() {
        converter = new AccessTokenRequestConverter(factory: new AccessTokenRequestFactory())
        request = Mock HttpServletRequest

    }

    def 'converts an http request to an implicit grant request'() {
        given:
        1 * this.request.getParameter('grant_type') >> 'authorization_code'
        1 * this.request.getParameter('redirect_uri') >> 'https://test.com/callback'
        1 * this.request.getParameter('code') >> 'ju92f2h'

        when:
        AccessTokenAuthorizationCodeFlowRequest accessTokenRequest = converter.convert request

        then:
        with(accessTokenRequest) {
            redirectUri == new URI('https://test.com/callback')
            grantType == 'authorization_code'
            authorizationCode == 'ju92f2h'
        }
    }

    def 'should throw a missing param exception when a required param is not given'() {
        when:
        converter.convert request

        then:
        thrown MissingQueryParamException
    }

    def 'should NOT throw a missing param exception when an optional param is not given'() {
        given:
        1 * this.request.getParameter('grant_type') >> 'authorization_code'
        1 * this.request.getParameter('code') >> 'ju92f2h'

        when:
        converter.convert request

        then:
        notThrown MissingQueryParamException
    }


}
