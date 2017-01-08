package uk.co.pragmaticdevelopers.gate

import uk.co.pragmaticdevelopers.gate.factory.GrantRequestFactory
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitGrantRequest
import spock.lang.Specification
import spock.lang.Subject
import uk.co.pragmaticdevelopers.gate.httpconverter.GrantConverter

import javax.servlet.http.HttpServletRequest

class GrantConverterSpec extends Specification {

    @Subject GrantConverter converter

    def setup() {
        converter = new GrantConverter(grantRequestFactory: new GrantRequestFactory())
    }

    def 'converts an http request to an implicit grant request'() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest) {
            getQueryString() >> 'response_type=token&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Ftest%2Ecom%2Fcallback&scope=test'
        }

        when:
        ImplicitGrantRequest implicitGrantRequest = converter.convert request

        then:
        with(implicitGrantRequest) {
            redirectUri == 'https://test.com/callback'
            responseType == 'token'
            state == 'xyz'
            scope == 'test'
            clientId == 's6BhdRkqt3'
        }
    }

    def 'should throw a missing param exception when a required param is not given'() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest) {
            getQueryString() >> 'client_id=s6BhdRkqt3'
        }

        when:
        converter.convert request

        then:
        thrown MissingQueryParamException
    }

    def 'should NOT throw a missing param exception when an optional param is not given'() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest) {
            getQueryString() >> 'response_type=token&client_id=s6BhdRkqt3&state=xyz'
        }

        when:
        converter.convert request

        then:
        notThrown MissingQueryParamException
    }

}
