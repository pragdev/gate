package uk.co.pragmaticdevelopers.gate.appengine

import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.*

class OAuthEvents implements uk.co.pragmaticdevelopers.gate.OAuthEvents {

    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestRepository accessRequestRepository
    TokenRepository tokenRepository

    void onAccessTokenIssued(AccessToken token, Credentials credentials) {
        tokenRepository.store token
    }

    void onNewClientRegistered(Client client) {
        clientsRepository.store client
    }

    void onNewResourceOwner(ResourceOwner owner, AccessToken token) {
        resourceOwnerRepository.store owner
        tokenRepository.store token
    }

    void onNewAccessRequest(AccessRequest accessRequest) {
        accessRequestRepository.store accessRequest
    }

    void onGrantedAccess(storedAccessRequest) {
        accessRequestRepository.store storedAccessRequest
    }
}
