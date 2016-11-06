package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.tools.remoteapi.RemoteApiInstaller
import com.google.appengine.tools.remoteapi.RemoteApiOptions
import groovyx.net.http.RESTClient

class MyWorld {
  public RESTClient restClient
  private DatastoreService datastoreService
  private CredentialsRepository credentialsRepository
  static RemoteApiOptions remoteOptions = new RemoteApiOptions()
    .server('localhost', 8080)
    .useDevelopmentServerCredential()

  MyWorld() {
    println "called "*10
    restClient = new RESTClient('http://localhost:8080/')

    datastoreService = DatastoreServiceFactory.datastoreService
    credentialsRepository = new CredentialsRepository(datastore: datastoreService)
  }

  def store(Credentials credentials) {
    // TODO try storing an access token only !?
    this.credentialsRepository.store credentials
  }

  def store(AccessToken accessToken) {
    credentialsRepository.store accessToken
  }
}