package goauth

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
}
