package goauth

import groovy.json.JsonBuilder

class Presenter {

    String present(AccessToken token) {
        def builder = new JsonBuilder()
        builder {
            access_token token.value
            token_type 'bearer'
            expires_in token.expiresIn
        }

        builder.toString()
    }
}
