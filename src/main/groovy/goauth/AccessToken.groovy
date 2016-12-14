package goauth

import groovy.transform.Canonical

import java.lang.Long as Seconds

@Canonical
class AccessToken {
    String value
    Date issuedOn
    Seconds expiresIn

    AccessToken(Map properties) {
        this(properties.issuedOn, properties.expiresIn)
    }

    AccessToken(Date issuedOn, Seconds expiresIn) {
        this.value = UUID.randomUUID().toString()
        this.issuedOn = issuedOn
        this.expiresIn = expiresIn
    }

    AccessToken() {
        this(new Date(), 3600)
    }

    boolean isExpired() {
        def now = new Date()
        now.after expiryDate()
    }

    private Date expiryDate() {
        new Date(issuedOn.time + expiresIn * 1000)
    }

}
