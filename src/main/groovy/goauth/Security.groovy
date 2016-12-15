package goauth

import groovy.util.logging.Log

import static goauth.AccessRequest.Status.DENIED
import static goauth.AccessRequest.Status.GRANTED

enum AuthenticationFlow {
    CLIENT_CREDENTIALS, PASSWORD
}

@Log
class Security {
    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestRepository accessRequestRepository

    AccessToken authenticateResourceOwner(Credentials credentials) throws InvalidCredentialsException {
        if (!resourceOwnerRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

        ResourceOwner storedResourceOwner = resourceOwnerRepository.findBy credentials.username
        if (!storedResourceOwner.accept(credentials)) throw new InvalidCredentialsException(credentials)

        resourceOwnerRepository.store new AccessToken()
    }

    AccessToken authenticateClient(Credentials credentials) throws InvalidCredentialsException {
        if (!clientsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

        Credentials storedCredentials = clientsRepository.findBy(credentials.username).credentials
        if (storedCredentials != credentials) throw new InvalidCredentialsException(credentials)

        resourceOwnerRepository.store new AccessToken()
    }

    Client register(Client client) {
        client.id = UUID.randomUUID().toString()
        client.secret = UUID.randomUUID().toString()

        clientsRepository.store client
    }

    Client findClientBy(id) {
        clientsRepository.findBy id
    }

    ResourceOwner identifyResourceOwnerBy(Credentials credentials) {
        if (!credentials || credentials.incomplete) throw new InvalidCredentialsException(credentials)

        ResourceOwner resourceOwner = resourceOwnerRepository.findBy credentials.username
        if (!resourceOwner?.accept(credentials)) throw new InvalidCredentialsException(credentials)

        return resourceOwner
    }

    AccessRequest accessRequest(Client client, ResourceOwner resourceOwner) {
        // TODO test store is called
        accessRequestRepository.store new AccessRequest(client: client, resourceOwner: resourceOwner)
    }

    AccessToken grantAccess(AccessRequest accessRequest) {
        if (!(accessRequest.status in [GRANTED, DENIED])) throw new InvalidStatusException()
        if (!accessRequestRepository.exists(accessRequest.id)) throw new EntityNotFound()

        def storedAccessRequest = accessRequestRepository.findBy accessRequest.id
        storedAccessRequest.status = GRANTED
        accessRequestRepository.store storedAccessRequest
        new AccessToken()
    }

    def redirectUriFor(String accessRequestId) {
        def accessRequest = accessRequestRepository.findBy accessRequestId
        accessRequest.client.redirectionUri
    }
}
