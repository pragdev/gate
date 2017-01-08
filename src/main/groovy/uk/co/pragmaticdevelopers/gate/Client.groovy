package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical

@Canonical
class Client {

    public enum Type {
        CONFIDENTIAL, PUBLIC
    }

    String id, secret, name
    URI redirectionUri
    Type type

    boolean accept(Credentials credentials) {
        id == credentials.username && secret == credentials.password
    }

    Credentials getCredentials() {
        return new Credentials(username: id, password: secret)
    }

}