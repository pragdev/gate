package uk.co.pragmaticdevelopers.gate

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class HttpServletRequestExtensionSpec extends Specification {

    HttpServletRequestExtension extension = new HttpServletRequestExtension()

    def "should extract credentials from the Authorization header"() {
        given:

        HttpServletRequest request = Mock(HttpServletRequest) {
            getHeader('Authorization') >> 'Basic bXlhcHA6dGVzdA=='
        }

        when:
        def credentials = extension.extractCredentialsFromHeader request

        then:
        credentials == new Credentials('myapp', 'test')
    }

    def "should not extract any credentials from the Authorization header if params are missing"() {
        given:

        HttpServletRequest request = Mock(HttpServletRequest) {
            getHeader('Authorization') >> header
        }

        expect:
        !extension.extractCredentialsFromHeader(request)

        where:
        header << ['', null, 'Basic wrong9hjformat', 'Basic ', 'wrong']
    }

    def 'should provide a map of query string params'() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest) {
            getQueryString() >> "response_type=token&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb"
        }

        when:
        Map queryParams = extension.queryParams(request)

        then:
        queryParams.response_type == 'token'
        queryParams.client_id == 's6BhdRkqt3'
        queryParams.state == 'xyz'
        queryParams.redirect_uri == 'https://client.example.com/cb'
    }
}
