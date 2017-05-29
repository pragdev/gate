package uk.co.pragmaticdevelopers.gate.appengine

import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.*

@Log
class Context implements uk.co.pragmaticdevelopers.gate.Context {

    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestRepository accessRequestRepository
    TokenRepository tokenRepository

    AccessRequest findAccessRequest(AccessRequest accessRequest) {
        accessRequestRepository.findBy accessRequest.id
    }

    Client findClientBy(id) {
        clientsRepository.findBy id
    }

    ResourceOwner findResourceOwner(String username) {
        log.info "OAUTH context, searching for resource owner $username"
        resourceOwnerRepository.findBy username
    }

    ResourceOwner findResourceOwner(Credentials credentials) {
        resourceOwnerRepository.findBy credentials.username
    }

    Client findClient(Credentials credentials) {
        clientsRepository.findBy(credentials.username)
    }

    @Override
    Token findToken(String authorizationCode, Class<? extends Token> type) {
        tokenRepository.findBy authorizationCode, AuthorizationCode

    }

    @Override
    AccessToken makeAccessToken(Client client) {
        new AccessToken()
    }

    @Override
    AccessToken makeAccessToken(ResourceOwner owner) {
        new AccessToken()
    }
}
