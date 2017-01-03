package goauth

import goauth.authorizationcodegrant.AuthorizationCodeGrantRequest
import goauth.implicitgrant.ImplicitGrantRequest
import spock.lang.Specification
import spock.lang.Unroll

import static goauth.AccessRequest.Status.GRANTED
import static goauth.AccessRequest.Status.PENDING
import static goauth.Client.Type.CONFIDENTIAL

class SecuritySpec extends Specification {
    private Security security

    def setup() {
        security = new Security()
        security.clientsRepository = Mock(ClientRepository) {
            findBy('client1') >> new Client(id: 'client1', secret: 'secret')
            exists('client1') >> true
        }
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            findBy('antonio') >> new ResourceOwner(username: 'antonio', password: 'secret', displayName: "ayeye brazorf")
            exists('antonio') >> true
        }
        security.accessRequestRepository = Mock(AccessRequestRepository)
        security.accessRequestFactory = new AccessRequestFactory()
        security.tokenRepository = Mock(TokenRepository) {
            store(_ as AccessToken) >> { args -> args.first() }
        }

    }

    def "should throw an invalid credential exception when the username is not present in the repository"() {
        given:
        0 * security.tokenRepository.store(_ as AccessToken)
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            exists('wrongusername') >> false
        }

        def credentials = new Credentials(username: 'wrongusername', password: 'secret')

        when:
        security.authenticateResourceOwner credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credential exception when the user password does not match the stored password"() {
        given:
        0 * security.tokenRepository.store(_ as AccessToken)
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            exists('antonio') >> true
            findBy('antonio') >> new ResourceOwner(username: 'antonio', password: 'secret', displayName: 'Ayeye Brazorf')
        }

        def credentials = new Credentials(username: 'antonio', password: 'wrong')

        when:
        security.authenticateResourceOwner credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should authenticate the resource owner credentials"() {
        given:
        0 * security.tokenRepository.store(_ as AccessToken)
        def credentials = new Credentials(username: 'antonio', password: 'secret')

        when:
        security.authenticateResourceOwner credentials

        then:
        notThrown Exception
    }

    def "should throw an invalid credential exception when the client name is not present in the repository"() {
        given:
        0 * security.tokenRepository.store(_ as AccessToken)
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            exists('wrongclientname') >> false
        }

        when:
        security.authenticateResourceOwner new Credentials(username: 'wrongclientname', password: 'secret')

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credential exception when the client password does not match the stored password"() {
        given:
        0 * security.tokenRepository.store(_ as AccessToken)
        def credentials = new Credentials(username: 'myapp', password: 'wrong')

        when:
        security.authenticateResourceOwner credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should register a new client on the system"() {
        given:
        def client = new Client(name: 'myapp', redirectionUri: new URI('http://myapp.com/grabtoken'), type: CONFIDENTIAL)
        security.clientsRepository = GroovyMock(ClientRepository) {
            store(client) >> client
        }

        when:
        security.register client

        then:
        client.id ==~ /[A-z0-9-]+/
        client.secret ==~ /[A-z0-9-]+/

        1 * security.clientsRepository.store(client)
    }

    def "should identify a resource owner by credentials"() {
        given:
        def expectedOwner = new ResourceOwner(username: 'user', password: 'secret', displayName: "Ayeye Brazorf")
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            findBy(expectedOwner.username) >> expectedOwner
        }

        when:
        ResourceOwner resourceOwner = security.identifyResourceOwnerBy expectedOwner.credentials

        then:
        resourceOwner == expectedOwner
    }

    def "should throw an invalid credentials exception when resource owner's credentials are invalid"() {
        given:
        def expectedOwner = new ResourceOwner(username: 'user', password: 'secret', displayName: "Ayeye Brazorf")
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) {
            findBy(expectedOwner.username) >> storedOwner
        }

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
        security.accessRequestRepository = Mock(AccessRequestRepository) {
            exists(_) >> true
            findBy(_) >> new ImplicitFlowAccessRequest(id: "existingid", status: GRANTED, client: new Client())
        }
        def accessRequest = new ImplicitFlowAccessRequest(id: "existingid", status: GRANTED)

        when:
        def accessToken = security.grantAccess accessRequest

        then:
        1 * security.accessRequestRepository.store({ it.status == GRANTED })
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
        security.accessRequestRepository = Mock(AccessRequestRepository) {
            exists(_) >> false
        }
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

        security.accessRequestRepository = Mock(AccessRequestRepository) {
            1 * findBy(_) >> accessRequest
        }

        expect:
        security.redirectUriFor(accessRequest) == expectedUri
    }

    def "should deny an access request when the client cannot be identified when issuing an access request for implicit flow"() {
        given:
        def grantRequest = new ImplicitGrantRequest(clientId: 'client1', responseType: 'token')
        security.clientsRepository = Mock(ClientRepository) { findBy('client1') >> null }
        def credentials = new Credentials(username: 'test', password: 'secret')


        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown EntityNotFound
    }

    def "should deny an access request when the resource owner cannot be identified when issuing an access request for implicit flow"() {
        given:
        def grantRequest = new ImplicitGrantRequest(clientId: 'client1', responseType: 'token')
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) { findBy('test') >> new ResourceOwner() }

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
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'client1', responseType: 'code')
        security.clientsRepository = Mock(ClientRepository) { findBy('client1') >> null }
        def credentials = new Credentials(username: 'test', password: 'secret')

        when:
        security.issueAccessRequest(grantRequest, credentials)

        then:
        thrown EntityNotFound
    }

    def "should deny an access request when the resource owner cannot be identified when issuing an access request for authorization flow"() {
        given:
        def grantRequest = new AuthorizationCodeGrantRequest(clientId: 'client1', responseType: 'code')
        security.resourceOwnerRepository = Mock(ResourceOwnerRepository) { findBy('test') >> new ResourceOwner() }

        def credentials = new Credentials(username: 'antonio', password: 'secret')

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
        1 * security.accessRequestRepository.store(_ as AccessRequest) >> { it.first() }
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

    def 'issues a new access token for the authorization code flow'() {
        given:
        def codeFlowRequest = new AccessTokenAuthorizationCodeFlowRequest(grantType: 'authorization_code')
        def credentials = new Credentials(username: 'client1', password: 'secret')

        when:
        def accessToken = security.issueAccessToken(codeFlowRequest, credentials)

        then:
        accessToken.value ==~ /[A-z0-9-]+/
    }
}
