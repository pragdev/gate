package uk.co.pragmaticdevelopers.gate

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor
class Credentials implements Serializable {
    String username
    String password

    boolean isIncomplete() {
        !username?.trim() || !password?.trim()
    }

    String encode() {
        if(incomplete) throw new IllegalStateException()
        "${username}:${password}".bytes.encodeBase64().toString()
    }

}
