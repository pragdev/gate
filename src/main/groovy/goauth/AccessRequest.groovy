package goauth

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

import static goauth.AccessRequest.Status.PENDING

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
