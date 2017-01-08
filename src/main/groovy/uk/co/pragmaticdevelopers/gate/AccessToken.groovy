package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical

@Canonical
class AccessToken extends Token {

    @Override
    def describe() {
        ['access_token', value]
    }
}
