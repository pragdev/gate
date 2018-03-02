package uk.co.pragmaticdevelopers.gate

import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory
import uk.co.pragmaticdevelopers.gate.flow.authorizationcode.AccessTokenAuthorizationCodeFlowRequest
import uk.co.pragmaticdevelopers.gate.flow.clientcredentials.AccessTokenClientCredentialsFlowRequest
import uk.co.pragmaticdevelopers.gate.flow.resourceownerpasswordcredentials.AccessTokenPasswordFlowRequest

import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.DENIED
import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.GRANTED

enum AuthenticationFlow {
    CLIENT_CREDENTIALS, PASSWORD, REFRESH_TOKEN, AUTHORIZATION_CODE
}

@Log
class Security {
    AccessRequestFactory accessRequestFactory
    OAuthEvents events
    Context context

    def authenticateResourceOwner(Credentials credentials) throws InvalidCredentialsException {
        ResourceOwner storedResourceOwner = context.findResourceOwner(credentials)

        if (!storedResourceOwner) throw new InvalidCredentialsException(credentials)
        if (!storedResourceOwner?.accept(credentials)) throw new InvalidCredentialsException(credentials)

        return storedResourceOwner
    }

    def authenticateClient(Credentials credentials) throws InvalidCredentialsException {
        Client storedClient = context.findClient(credentials)

        if (!storedClient) throw new InvalidCredentialsException(credentials)
        if (!storedClient.accept(credentials)) throw new InvalidCredentialsException(credentials)

        return storedClient
    }

    AccessToken register(ResourceOwner owner) {
        AccessToken token = events.onNewResourceOwner(owner)
        return token
    }

    Client register(Client client) {
        client.id = UUID.randomUUID().toString()
        client.secret = UUID.randomUUID().toString()

        events.onNewClientRegistered(client)
        return client
    }

    ResourceOwner identifyResourceOwnerBy(Credentials credentials) {
        if (!credentials || credentials.incomplete) throw new InvalidCredentialsException(credentials)

        ResourceOwner storedOwner = context.findResourceOwner(credentials.username)
        if (!storedOwner?.accept(credentials)) throw new InvalidCredentialsException(credentials)

        return storedOwner
    }

    AccessRequest makeAccessRequestFor(Client client, ResourceOwner resourceOwner, GrantRequest grantRequest) {
        // TODO test onNewAccessRequest is called
        AccessRequest accessRequest = accessRequestFactory.make(client: client, resourceOwner: resourceOwner, grantRequest: grantRequest)
        events.onNewAccessRequest(accessRequest)
        return accessRequest
    }

    Token grantAccess(AccessRequest accessRequest) {
        if (!(accessRequest.status in [GRANTED, DENIED])) throw new InvalidStatusException()
        def storedAccessRequest = context.findAccessRequest(accessRequest)

        if (!storedAccessRequest) throw new EntityNotFound()

        def token = storedAccessRequest.grant()
        events.onGrantedAccess(storedAccessRequest)

        return token
    }

    URI redirectUriFor(AccessRequest accessRequest) {
        def request = context.findAccessRequest(accessRequest)
        request.client.redirectionUri
    }

    AccessRequest issueAccessRequest(GrantRequest grantRequest, Credentials credentials) {
        if (!grantRequest.validType) throw new InvalidResponseTypeException()

        Client client = context.findClientBy grantRequest.clientId
        if (!client) throw new EntityNotFound()

        def owner = identifyResourceOwnerBy credentials

        makeAccessRequestFor(client, owner, grantRequest)
    }

    AccessToken issueAccessToken(AccessTokenRequest tokenRequest, Credentials credentials) {
        if (!tokenRequest.validType) throw new InvalidGrantTypeException()

        def authenticatedSubject = tokenRequest.authenticate credentials, this
        handle tokenRequest

        def token = context.makeAccessToken(authenticatedSubject)
        events.onAccessTokenIssued(token, credentials)
        return token
    }

    def handle(AccessTokenAuthorizationCodeFlowRequest request) {
        def token = context.findToken(request.authorizationCode, AuthorizationCode)
        if (!token || token.expired) throw new InvalidTokenException()
    }

    def handle(AccessTokenPasswordFlowRequest request) {}

    def handle(AccessTokenClientCredentialsFlowRequest request) {}

}
