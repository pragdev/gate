package uk.co.pragmaticdevelopers.gate.presenter

import uk.co.pragmaticdevelopers.gate.AccessToken
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Subject

class PresenterTest extends Specification {

    @Subject Presenter presenter = new Presenter()

    def 'should present an access token in json'() {
        given:
        def token = new AccessToken(value: 'abc', expiresIn: 3600)

        when:
        def json = presenter.present token

        then:
        prettyJson(json) == prettyJson("""
        {
            "access_token": "abc",
            "token_type": "bearer",
            "expires_in": 3600
        }
        """)
    }

    def prettyJson(String json) {
        parse(json).toString()
    }

    def parse(json) {
        new JsonSlurper().parseText(json)
    }
}
