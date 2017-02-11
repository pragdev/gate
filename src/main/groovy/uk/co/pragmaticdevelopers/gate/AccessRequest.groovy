package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.GRANTED
import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.PENDING

@TupleConstructor
@Canonical
class AccessRequest {

    String id = UUID.randomUUID().toString()
    public static enum Status {
        PENDING, GRANTED, DENIED
    }

    Token grant() {
        status = GRANTED
        token = generateToken()
        return token
    }

    Status status = PENDING

    Token token
    ResourceOwner resourceOwner
    Client client

    Token generateToken() { null }
}
