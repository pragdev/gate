package goauth

import spock.lang.Specification
import spock.lang.Unroll

import static goauth.AccessRequest.Status.GRANTED
import static goauth.AccessRequest.Status.PENDING
import static goauth.Client.Type.CONFIDENTIAL

class SecuritySpec extends Specification {
    private Security security

    def setup() {
        security = new Security()
    }

    def "should provide a new Access Token when user credentials are authenticated"() {
        given:
        security.credentialsRepository = Mock(CredentialsRepository) {
            1 * exists('antonio') >> true
            1 * findBy('antonio') >> new Credentials(username: 'antonio', password: 'test')
            1 * store(_ as AccessToken) >> { args -> args.first() }
        }

        def credentials = new Credentials(username: 'antonio', password: 'test')

        when:
        AccessToken accessToken = security.authenticate credentials

        then:
        accessToken
    }

    def "should throw an invalid credential exception when the username is not present in the repository"() {
        given:

        security.credentialsRepository = Mock(CredentialsRepository) {
            exists('wrongusername') >> false
            0 * store(_ as AccessToken)
        }

        def credentials = new Credentials(username: 'wrongusername', password: 'test')

        when:
        security.authenticate credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credential exception when the user password does not match the stored password"() {
        given:

        security.credentialsRepository = Mock(CredentialsRepository) {
            exists('antonio') >> true
            findBy('antonio') >> new Credentials(username: 'antonio', password: 'test')
            0 * store(_ as AccessToken)
        }

        def credentials = new Credentials(username: 'antonio', password: 'wrong')

        when:
        security.authenticate credentials

        then:
        thrown InvalidCredentialsException
    }

    def "should provide a new Access Token when client credentials are authenticated"() {
        given:

        security.credentialsRepository = Mock(CredentialsRepository) {
            1 * exists('myapp') >> true
            1 * findBy('myapp') >> new Credentials(username: 'myapp', password: 'test')
            1 * store(_ as AccessToken) >> { args -> args.first() }
        }
        def credentials = new Credentials(username: 'myapp', password: 'test')

        when:
        AccessToken accessToken = security.authenticate credentials

        then:
        accessToken
    }

    def "should throw an invalid credential exception when the client name is not present in the repository"() {
        given:

        security.credentialsRepository = Mock(CredentialsRepository) {
            exists('wrongclientname') >> false
            0 * store(_ as AccessToken)
        }

        when:
        security.authenticate new Credentials(username: 'wrongclientname', password: 'test')

        then:
        thrown InvalidCredentialsException
    }

    def "should throw an invalid credential exception when the client password does not match the stored password"() {
        given:

        security.credentialsRepository = Mock(CredentialsRepository) {
            exists('myapp') >> true
            findBy('myapp') >> new Credentials(username: 'myapp', password: 'test')
            0 * store(_ as AccessToken)
        }

        def credentials = new Credentials(username: 'myapp', password: 'wrong')

        when:
        security.authenticate credentials

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
        def credentials = new Credentials(username: 'user', password: 'test')
        security.credentialsRepository = Mock(CredentialsRepository) {
            findBy(credentials.username) >> credentials
        }

        when:
        ResourceOwner resourceOwner = security.identifyResourceOwnerBy credentials

        then:
        resourceOwner != null
        resourceOwner.name == credentials.username
    }

    def "should throw an invalid credentials exception when resource owner's credentials are invalid"() {
        given:
        def credentials = new Credentials(username: 'user', password: 'test')
        security.credentialsRepository = Mock(CredentialsRepository) {
            findBy(credentials.username) >> storedCredentials
        }

        when:
        security.identifyResourceOwnerBy credentials

        then:
        thrown InvalidCredentialsException

        where:
        storedCredentials << [new Credentials(username: 'user', password: 'different'), null]
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
            findBy(_) >> new AccessRequest(id: "existingid", status: GRANTED, client: new Client())
        }
        def accessRequest = new AccessRequest(id: "existingid", status: GRANTED)

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
        security.accessRequestRepository = Mock(AccessRequestRepository) {
            1 * findBy(_) >> new AccessRequest(id: '123', client: new Client(redirectionUri: expectedUri))
        }

        when:
        def uri = security.redirectUriFor '123'

        then:
        uri == expectedUri
    }
}
