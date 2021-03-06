package uk.co.pragmaticdevelopers.gate

import com.google.appengine.api.datastore.DatastoreServiceFactory
import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.factory.AccessRequestFactory
import uk.co.pragmaticdevelopers.gate.factory.AccessTokenRequestFactory
import uk.co.pragmaticdevelopers.gate.factory.GrantRequestFactory
import uk.co.pragmaticdevelopers.gate.factory.TokenFactory
import uk.co.pragmaticdevelopers.gate.httpconverter.AccessTokenRequestConverter
import uk.co.pragmaticdevelopers.gate.httpconverter.GrantConverter
import uk.co.pragmaticdevelopers.gate.presenter.Presenter

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
        def tokenRepository = new TokenRepository(datastore: datastoreService, tokenFactory: new TokenFactory())
        def factory = new AccessRequestFactory()
        def accessRequestRepository = new AccessRequestRepository(
                datastore: datastoreService,
                resourceOwnerRepository: resourceOwnerRepository,
                clientsRepository: clientsRepository,
                accessRequestFactory: factory
        )
        def security = new Security(
                context: new uk.co.pragmaticdevelopers.gate.appengine.Context (
                        resourceOwnerRepository: resourceOwnerRepository,
                        clientsRepository: clientsRepository,
                        accessRequestRepository: accessRequestRepository,
                        tokenRepository: tokenRepository
                ),
                events: new uk.co.pragmaticdevelopers.gate.appengine.OAuthEvents(
                        resourceOwnerRepository: resourceOwnerRepository,
                        clientsRepository: clientsRepository,
                        accessRequestRepository: accessRequestRepository,
                        tokenRepository: tokenRepository
                ),
                accessRequestFactory: factory
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
