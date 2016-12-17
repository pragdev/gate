package goauth.implicitgrant

import goauth.MissingQueryParam
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

class ImplicitGrantConverterSpec extends Specification {

    @Subject ImplicitGrantConverter converter = new ImplicitGrantConverter()

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
        thrown MissingQueryParam
    }

    def 'should NOT throw a missing param exception when an optional param is not given'() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest) {
            getQueryString() >> 'response_type=token&client_id=s6BhdRkqt3&state=xyz'
        }

        when:
        converter.convert request

        then:
        notThrown MissingQueryParam
    }

}
