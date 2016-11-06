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
    def security = new Security(credentialsRepository: credentialsRepository, clientsRepository: clientsRepository)

    event.servletContext.setAttribute('security', security)
    log.info 'services loaded'
  }

  @Override
  void contextDestroyed(ServletContextEvent event) {
  }
}
