package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.tools.remoteapi.RemoteApiOptions
import groovyx.net.http.RESTClient

class MyWorld {
    public RESTClient restClient
    private DatastoreService datastoreService
    private ResourceOwnerRepository resourceOwnerRepository
    private TokenRepository tokenRepository
    private ClientRepository clientRepository
    static RemoteApiOptions remoteOptions = new RemoteApiOptions()
            .server('localhost', 8080)
            .useDevelopmentServerCredential()

    MyWorld() {
        restClient = new RESTClient('http://localhost:8080/')

        datastoreService = DatastoreServiceFactory.datastoreService
        resourceOwnerRepository = new ResourceOwnerRepository(datastore: datastoreService)
        clientRepository = new ClientRepository(datastore: datastoreService)
        tokenRepository = new TokenRepository(datastore: datastoreService)
    }

    def store(ResourceOwner resourceOwner) {
        // TODO try storing an access token only !?
        this.resourceOwnerRepository.store resourceOwner
    }

    def store(AccessToken accessToken) {
        tokenRepository.store accessToken
    }

    def store(Client client) {
        clientRepository.store client
    }

    def store(AuthorizationCode code) {
        tokenRepository.store code
    }
}