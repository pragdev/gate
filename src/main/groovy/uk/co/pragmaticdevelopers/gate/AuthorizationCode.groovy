package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical

@Canonical
class AuthorizationCode extends Token {

    @Override
    def describe() {
        ['code', value]
    }
}
