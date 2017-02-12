package uk.co.pragmaticdevelopers.gate

import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AccessTokenAuthorizationCodeFlowRequest
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AuthorizationCodeGrantRequest
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitGrantRequest

import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.GRANTED
import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.PENDING
import static uk.co.pragmaticdevelopers.gate.Client.Type.CONFIDENTIAL

class SecuritySpec extends Specification {
    private Security security

    def setup() {
        security = new Security(accessRequestFactory: new AccessRequestFactory())
        security.context = Mock(Context) {
            findClientBy('client1') >> new Client(id: 'client1', secret: 'secret')
            findResourceOwner('antonio') >> new ResourceOwner(username: 'antonio', password: 'secret', displayName: "ayeye brazorf")
        }
        security.events = Mock OAuthEvents
    }

    def "should throw an invalid credential exception when the username is not present in the repository"() {
        given:
        security.context.findResourceOwner('wrongusername') >> false

        def credentials = new Credentials(username: 'wrongusername', password: 'secret')

        when:
        security.authenticateResourceOwner credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credential exception when the user password does not match the stored password"() {
        given:
        security.context.findResourceOwner('antonio') >> new ResourceOwner(username: 'antonio', password: 'secret', displayName: 'Ayeye Brazorf')

        def credentials = new Credentials(username: 'antonio', password: 'wrong')

        when:
        security.authenticateResourceOwner credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should authenticate the resource owner credentials"() {
        given:
        def credentials = new Credentials(username: 'antonio', password: 'secret')
        def resourceOwner = Mock(ResourceOwner) { accept(credentials) >> true }
        security.context.findResourceOwner(credentials) >> resourceOwner

        when:
        security.authenticateResourceOwner credentials

        then:
        notThrown Exception
    }

    def "should throw an invalid credential exception when the client name is not present in the repository"() {
        given:
        security.context.findClientBy('wrongclientname') >> false

        when:
        security.authenticateClient new Credentials(username: 'wrongclientname', password: 'secret')

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credentials exception when the client password does not match the stored password"() {
        given:
        def credentials = new Credentials(username: 'myapp', password: 'wrong')
        security.context.findClientBy(credentials) >> null

        when:
        security.authenticateClient credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should register a new client on the system"() {
        given:
        def client = new Client(name: 'myapp', redirectionUri: new URI('http://myapp.com/grabtoken'), type: CONFIDENTIAL)

        when:
        security.register client

        then:
        client.id ==~ /[A-z0-9-]+/
        client.secret ==~ /[A-z0-9-]+/

        1 * security.events.onNewClientRegistered(client)
    }

    def "should identify a resource owner by credentials"() {
        given:
        def expectedOwner = new ResourceOwner(username: 'user', password: 'secret', displayName: "Ayeye Brazorf")
        security.context.findResourceOwner(expectedOwner.username) >> expectedOwner

        when:
        ResourceOwner resourceOwner = security.identifyResourceOwnerBy expectedOwner.credentials

        then:
        resourceOwner == expectedOwner
    }

    def "should throw an invalid credentials exception when resource owner's credentials are invalid"() {
        given:
        def expectedOwner = new ResourceOwner(username: 'user', password: 'secret', displayName: "Ayeye Brazorf")
        security.context.findResourceOwner(expectedOwner.username) >> storedOwner


        when:
        security.identifyResourceOwnerBy expectedOwner.credentials

        then:
        thrown InvalidCredentialsException

        where:
        storedOwner << [new ResourceOwner(username: 'user', password: 'different', displayName: 'Ayeye Brazorf'), null]
    }

    @Unroll
    def "should throw an invalid credentials exception when resource owner's credentials are missing (#credentials)"() {
        when:
        security.identifyResourceOwnerBy credentials

        then:
        thrown InvalidCredentialsException

        where:
        credentials << [new Credentials(username: 'user'), new Credentials(password: 'user'), null]
    }

    def "should grant access to resources"() {
        given:
        security.context.findAccessRequest(_) >> new ImplicitFlowAccessRequest(id: "existingid", status: GRANTED, client: new Client())
        def accessRequest = new ImplicitFlowAccessRequest(id: "existingid", status: GRANTED)

        when:
        def accessToken = security.grantAccess accessRequest

        then:
        1 * security.events.onGrantedAccess({ it.status == GRANTED })
        accessToken.value ==~ /[0-9A-z-]*/
    }

    def "should respond with an invalid status exception when updating an access request with a status diffent from GRANTED or DENIED"() {
        given:
        def accessRequest = new AccessRequest(id: "existingid", status: PENDING)

        when:
        security.grantAccess accessRequest

        then:
        thrown InvalidStatusException
    }

    def "should respond with an entity not found exception when updating an access request is not already stored in the repository"() {
        given:
        security.context.findAccessRequest(_) >> null

        def accessRequest = new AccessRequest(id: "non-existingid", status: GRANTED)

        when:
        security.grantAccess accessRequest

        then:
        thrown EntityNotFound
    }

    def "should provide redirection uri for an access request id"() {
        given:
        def expectedUri = new URI('http/test.com/callback')
        def accessRequest = new ImplicitFlowAccessRequest(id: '123', client: new Client(redirectionUri: expectedUri))

        security.context.findAccessRequest(_) >> accessRequest


        expect:
        security.redirectUriFor(accessRequest) == expectedUri
    }

    def "should deny an access request when the client cannot be identified when issuing an access request for implicit flow"() {
        given:
        def grantRequest = new ImplicitGrantRequest(clientId: 'non-existing-client', responseType: 'token')
        security.context.findClientBy('non-existing-client') >> null
        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown EntityNotFound
    }

    def "should deny an access request when the resource owner cannot be identified when issuing an access request for implicit flow"() {
        given:
        def grantRequest = new ImplicitGrantRequest(clientId: 'client1', responseType: 'token')
        security.context.findResourceOwner('test') >> new ResourceOwner()

        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown InvalidCredentialsException

        where:
        authHeader << ["Basic ${"user:pass".bytes.encodeBase64().toString()}", null]
    }

    def 'should throw an exception when response type is not token when issuing an access request for implicit flow'() {
        given:
        def grantRequest = new ImplicitGrantRequest(clientId: 'client1', responseType: 'code')
        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown InvalidResponseTypeException
    }

    def "should deny an access request when the client cannot be identified when issuing an access request for authorization code flow"() {
        given:
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'non-existing-client', responseType: 'code')
        security.context.findClientBy('non-existing-client') >> null
        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown EntityNotFound
    }

    def "should deny an access request when the resource owner cannot be identified when issuing an access request for authorization flow"() {
        given:
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'client1', responseType: 'code')
        security.context.findResourceOwner('test') >> new ResourceOwner(username: 'different', password: 'diff3r3nt')

        def credentials = new Credentials(username: 'andrea', password: 'secr3t')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown InvalidCredentialsException

        where:
        authHeader << ["Basic ${"user:pass".bytes.encodeBase64().toString()}", null]
    }

    def 'should throw an exception when response type is not code for the authorization code grant'() {
        given:
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'client1', responseType: 'token')
        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown InvalidResponseTypeException
    }

    def 'should provide an Access Request for the authorization code grant'() {
        given:
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'client1', responseType: 'code')
        def credentials = new Credentials(username: 'antonio', password: 'secret')

        when:
        AccessRequest accessRequest = security.issueAccessRequest(grantRequest, credentials)

        then:
        with(accessRequest) {
            client.id == 'client1'
            resourceOwner.credentials == credentials
            id =~ /[A-z0-9-]+/
            status == PENDING
        }
        1 * security.events.onNewAccessRequest(_ as AccessRequest) >> { it.first() }
    }

    def 'should throw an invalid grant type when it does not match authorization_code when issuing an access token for the authorization code flow'() {
        given:
        def codeFlowRequest = new AccessTokenAuthorizationCodeFlowRequest(grantType: grantType)
        def credentials = new Credentials(username: 'antonio', password: 'secret')

        when:
        security.issueAccessToken(codeFlowRequest, credentials)

        then:
        thrown InvalidGrantTypeException

        where:
        grantType << ['wrong type', 'password']
    }

    def 'should throw an invalid token when the authorization code is not found when issuing an access token for the authorization code flow'() {
        given:
        def codeFlowRequest = new AccessTokenAuthorizationCodeFlowRequest(grantType: 'authorization_code', authorizationCode: 'ABC')
        def credentials = new Credentials(username: 'client1', password: 'secret')
        def client = Mock(Client) { accept(credentials) >> true }
        security.context.findClient(credentials) >> client
        security.context.findToken('ABC', AuthorizationCode) >> null

        when:
        security.issueAccessToken(codeFlowRequest, credentials)

        then:
        thrown InvalidTokenException
    }

    def 'should throw an invalid token when the authorization code is expired when issuing an access token for the authorization code flow'() {
        given:
        def codeFlowRequest = new AccessTokenAuthorizationCodeFlowRequest(grantType: 'authorization_code', authorizationCode: 'ABC')
        def credentials = new Credentials(username: 'client1', password: 'secret')
        def client = Mock(Client) { accept(credentials) >> true }
        security.context.findClient(credentials)  >> client
        security.context.findToken('ABC', AuthorizationCode) >> new AuthorizationCode(value: 'ABC', issuedOn: new Date() - 1)

        when:
        security.issueAccessToken(codeFlowRequest, credentials)

        then:
        thrown InvalidTokenException
    }

    def 'issues a new access token for the authorization code flow'() {
        given:
        def codeFlowRequest = new AccessTokenAuthorizationCodeFlowRequest(grantType: 'authorization_code', authorizationCode: 'ABC')
        def credentials = new Credentials(username: 'client1', password: 'secret')
        def client = Mock(Client) { accept(credentials) >> true }
        security.context.findClient(credentials)  >> client
        security.context.findToken('ABC', AuthorizationCode) >> new AuthorizationCode(value: 'ABC')

        when:
        def accessToken = security.issueAccessToken(codeFlowRequest, credentials)

        then:
        accessToken.value ==~ /[A-z0-9-]+/
    }
}
