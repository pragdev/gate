package uk.co.pragmaticdevelopers.gate.appengine

import uk.co.pragmaticdevelopers.gate.*

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
}
