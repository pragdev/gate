package goauth

import groovy.transform.Canonical

@Canonical
class Client {

  public enum Type { CONFIDENTIAL, PUBLIC }

  String id, secret, name
  URI redirectionUri
  Type type

}