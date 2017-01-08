package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

import static uk.co.pragmaticdevelopers.gate.AccessRequest.Status.PENDING

@TupleConstructor
@Canonical
class AccessRequest {
    String id = UUID.randomUUID().toString()

    public static enum Status {
        PENDING, GRANTED, DENIED
    }

    Status status = PENDING
    ResourceOwner resourceOwner
    Client client

    def makeToken() {}
}
