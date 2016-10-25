package goauth

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient

class MyWorld {
  public RESTClient restClient

  MyWorld() {
    restClient = new RESTClient( 'http://localhost:8080/' )

  }

  def store(Credentials credentials) {
    restClient.post(path: 'remotecontroller', body: JsonOutput.toJson(credentials), requestContentType: 'application/json')
  }
}