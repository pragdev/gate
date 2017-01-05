package goauth

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL
import static com.google.appengine.api.datastore.Query.FilterOperator.IN

class TokenRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService
    TokenFactory tokenFactory

    Token store(Token token) {
        Entity entity = Entity.make(token.value, Token)
        entity['value'] = token.value
        entity['issuedOn'] = token.issuedOn.time
        entity['expiresIn'] = token.expiresIn
        entity['type'] = token.class.simpleName

        datastore.put entity
        token
    }

    Token findBy(String value, Class type) {
        def entity = findTokenBy value, type
        if (!entity) return null

        tokenFactory.make(entity)
    }

    private Entity findTokenBy(String value, Class type) {

        def filter = new Query.CompositeFilter(
                Query.CompositeFilterOperator.AND,
                new ArrayList<Query.Filter>(Arrays.asList(
                        new Query.FilterPredicate('value', IN, [value]),
                        new Query.FilterPredicate('type', EQUAL, type.simpleName)
                ))
        )
        def query = datastore.prepare new Query(Token.simpleName)
                .setFilter(filter)

        query.asSingleEntity() ?: null
    }
}
