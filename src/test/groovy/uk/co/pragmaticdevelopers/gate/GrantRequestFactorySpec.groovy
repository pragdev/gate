package uk.co.pragmaticdevelopers.gate

import uk.co.pragmaticdevelopers.gate.factory.GrantRequestFactory
import spock.lang.Specification
import spock.lang.Subject
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeGrantRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitGrantRequest

class GrantRequestFactorySpec extends Specification {

    @Subject
    GrantRequestFactory grantRequestFactory = new GrantRequestFactory()

    def 'should thrown an invalid response type exception when response type is not recognized'() {
        when:
        grantRequestFactory.make(
                responseType: 'wrong',
                clientId: '123',
                redirectUri: 'http://test.com/callback',
                state: 'ab31lj',
                scope: 'manage_books')

        then:
        thrown InvalidResponseTypeException
    }

    def 'should create a implicit grant request when response type is token'() {
        when:
        ImplicitGrantRequest grantRequest = grantRequestFactory.make(
                responseType: 'token',
                clientId: '123',
                redirectUri: 'http://test.com/callback',
                state: 'ab31lj',
                scope: 'manage_books')

        then:
        grantRequest.responseType == 'token'
        grantRequest.clientId == '123'
        grantRequest.redirectUri == 'http://test.com/callback'
        grantRequest.state == 'ab31lj'
        grantRequest.scope == 'manage_books'
    }

    def 'should create an authorization code grant request when response type is code'() {
        when:
        AuthorizationCodeGrantRequest grantRequest = grantRequestFactory.make(
                responseType: 'code',
                clientId: '123',
                redirectUri: 'http://test.com/callback',
                state: 'ab31lj',
                scope: 'manage_books')

        then:
        grantRequest.responseType == 'code'
        grantRequest.clientId == '123'
        grantRequest.redirectUri == 'http://test.com/callback'
        grantRequest.state == 'ab31lj'
        grantRequest.scope == 'manage_books'
    }

    def 'should throw an illegal argument exception when there are arguments not expected for a grant request'() {
        when:
        ImplicitGrantRequest grantRequest = grantRequestFactory.make(
                responseType: 'code',
                clientId: '123',
                invalid: 'xxx',
                scope: 'manage_books')

        then:
        thrown MissingPropertyException
    }
}
