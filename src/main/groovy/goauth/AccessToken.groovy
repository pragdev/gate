package goauth

import groovy.transform.Canonical

import java.lang.Long as Seconds

@Canonical
class AccessToken extends Token {

    @Override
    def describe() {
        ['access_token', value]
    }
}
