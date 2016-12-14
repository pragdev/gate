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
        def credentialsRepository = new CredentialsRepository(datastore: datastoreService)
        def clientsRepository = new ClientRepository(datastore: datastoreService)
        def accessRequestRepository = new AccessRequestRepository(datastore: datastoreService, credentialsRepository: credentialsRepository, clientsRepository: clientsRepository)
        def security = new Security(credentialsRepository: credentialsRepository, clientsRepository: clientsRepository, accessRequestRepository: accessRequestRepository)

        event.servletContext.setAttribute('security', security)
        log.info 'services loaded'
    }

    @Override
    void contextDestroyed(ServletContextEvent event) {
    }
}
