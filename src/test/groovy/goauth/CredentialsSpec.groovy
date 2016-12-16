package goauth

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CredentialsSpec extends Specification {

    @Subject
    Credentials credentials

    def 'should be encodable'() {
        given:
        credentials = new Credentials(username: 'test', password: 'pass')

        expect:
        credentials.encode() == "dGVzdDpwYXNz"
    }

    @Unroll
    def 'should respond with an Illegal State Exception when username (#username) or password (#password) to encode are not present'() {
        given:
        credentials = new Credentials(username: username, password: password)

        when:
        credentials.encode()

        then:
        thrown IllegalStateException

        where:
        username | password
        ''       | 'pass'
        'test'   | ''
        null     | 'pass'
        'test'   | null
        '  '     | 'pass'
        'test'   | '  '
    }

}
