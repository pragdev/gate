package goauth

import com.google.appengine.api.datastore.Entity

class TokenFactory {

    Token make(Entity entity) {
        switch (entity['type']) {
            case 'AccessToken':
                return new AccessToken(
                        value: entity['value'],
                        issuedOn: new Date(entity['issuedOn']),
                        expiresIn: entity['expiresIn']
                )
                break
            case 'AuthorizationCode':
                return new AuthorizationCode(
                        value: entity['value'],
                        issuedOn: new Date(entity['issuedOn']),
                        expiresIn: entity['expiresIn']
                )
                break
            default:
                throw new IllegalArgumentException()
        }

    }
}