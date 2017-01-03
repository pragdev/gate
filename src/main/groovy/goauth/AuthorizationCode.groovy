package goauth

import groovy.transform.Canonical

@Canonical
class AuthorizationCode extends Token {

    @Override
    def describe() {
        ['code', value]
    }
}
