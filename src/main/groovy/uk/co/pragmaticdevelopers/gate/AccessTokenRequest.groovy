package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical

@Canonical
abstract class AccessTokenRequest {
    @Required String grantType

    AccessTokenRequest() {
        super()
        type()
    }

    protected abstract void type()
    abstract boolean isValidType()

    abstract void authenticate(Credentials credentials, Security security)
}
