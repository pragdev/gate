package goauth

import com.google.appengine.api.datastore.DatastoreServiceFactory
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
        def accessRequestRepository = new AccessRequestRepository(datastore: datastoreService, resourceOwnerRepository: resourceOwnerRepository, clientsRepository: clientsRepository)
        def security = new Security(resourceOwnerRepository: resourceOwnerRepository, clientsRepository: clientsRepository, accessRequestRepository: accessRequestRepository)

        event.servletContext.setAttribute('security', security)
        event.servletContext.setAttribute('presenter', new Presenter())
        log.info 'services loaded'
    }

    @Override
    void contextDestroyed(ServletContextEvent event) {
    }
}
