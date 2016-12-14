package goauth

import static goauth.AccessRequest.Status.PENDING

class AccessRequest {
    public static enum Status { PENDING, GRANTED, DENIED }

    String id = UUID.randomUUID().toString()
    Status status = PENDING
    ResourceOwner resourceOwner
    Client client
}
