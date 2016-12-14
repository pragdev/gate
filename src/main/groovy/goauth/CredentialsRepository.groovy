package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class CredentialsRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService

    Credentials store(Credentials credentials) {
        Entity entity = new Entity(asKey(credentials.username, 'Credentials'))
        entity.setProperty('username', credentials.username)
        entity.setProperty('password', credentials.password)

        datastore.put entity
        credentials
    }

    private Key asKey(String param, String kind) {
        def username = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, username)
    }

    boolean exists(String username) {
        findCredentialsBy username
    }

    Credentials findBy(String username) {
        def entity = findCredentialsBy username
        if (!entity) return null

        new Credentials(entity.getProperty('username').toString(), entity.getProperty('password').toString())
    }

    AccessToken store(AccessToken accessToken) {
        Entity entity = new Entity(asKey(accessToken.value, 'AccessToken'))
        entity.setProperty('value', accessToken.value)
        entity.setProperty('issuedOn', accessToken.issuedOn.time)
        entity.setProperty('expiresIn', accessToken.expiresIn)

        datastore.put entity
        accessToken
    }

    private Entity findCredentialsBy(String username) {
        def query = datastore.prepare new Query(Credentials.simpleName).setFilter(new Query.FilterPredicate('username', IN, [username]))
        query.asSingleEntity() ?: null
    }
}
