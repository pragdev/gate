package goauth

import com.google.appengine.api.datastore.DatastoreServiceFactory
import goauth.implicitgrant.GrantConverter
import groovy.util.logging.Log

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

@Log
class ContextListener implements ServletContextListener {

    @Override
    void contextInitialized(ServletContextEvent event) {
        log.info 'Context initialized, loading services'

        def datastoreService = DatastoreServiceFactory.datastoreService
        def resourceOwnerRepository = new ResourceOwnerRepository(datastore: datastoreService)
        def clientsRepository = new ClientRepository(datastore: datastoreService)
        def tokenRepository = new TokenRepository(datastore: datastoreService)
        def accessRequestRepository = new AccessRequestRepository(
                datastore: datastoreService,
                resourceOwnerRepository: resourceOwnerRepository,
                clientsRepository: clientsRepository,
                accessRequestFactory: new AccessRequestFactory()
        )
        def security = new Security(
                resourceOwnerRepository: resourceOwnerRepository,
                clientsRepository: clientsRepository,
                accessRequestRepository: accessRequestRepository,
                accessRequestFactory: new AccessRequestFactory(),
                tokenRepository: tokenRepository
        )
        def grantConverter = new GrantConverter(grantRequestFactory: new GrantRequestFactory())
        def accessTokenRequestConverter = new AccessTokenRequestConverter(factory: new AccessTokenRequestFactory())

        event.servletContext.setAttribute('security', security)
        event.servletContext.setAttribute('presenter', new Presenter())
        event.servletContext.setAttribute('grantConverter', grantConverter)
        event.servletContext.setAttribute('accessTokenRequestConverter', accessTokenRequestConverter)
        log.info 'services loaded'
    }

    @Override
    void contextDestroyed(ServletContextEvent event) {
    }
}
