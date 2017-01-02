package goauth

import com.google.appengine.repackaged.org.joda.time.Seconds
import groovy.transform.Canonical

@Canonical
class AuthorizationCode extends Token {
    String value
    Date issuedOn
    Long expiresIn

    AuthorizationCode() {
        this.value = UUID.randomUUID().toString()
        this.issuedOn = issuedOn
        this.expiresIn = expiresIn
    }

    boolean isExpired() {
        def now = new Date()
        now.after expiryDate()
    }

    private Date expiryDate() {
        new Date(issuedOn.time + expiresIn * 1000)
    }

    String toString() {
        this.value
    }

    @Override
    def describe() {
        ['code', value]
    }
}
