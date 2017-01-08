package uk.co.pragmaticdevelopers.gate

import groovy.time.TimeCategory
import spock.lang.Specification

class AccessTokenSpec extends Specification {

    def "should have a randomly generated value"() {
        given:
        def token1 = new AccessToken()
        def token2 = new AccessToken()

        expect:
        token1.value
        token2.value
        token1.value != token2.value
    }

    def "should expire in 3600 seconds"() {
        given:
        AccessToken token1
        AccessToken token2
        use(TimeCategory) {
            token1 = new AccessToken(issuedOn: 61.minutes.ago, expiresIn: 3600)
            token2 = new AccessToken(issuedOn: 59.minutes.ago, expiresIn: 3600)
        }

        expect:
        token1.expired
        !token2.expired
    }
}
