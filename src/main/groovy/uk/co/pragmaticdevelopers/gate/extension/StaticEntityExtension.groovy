package uk.co.pragmaticdevelopers.gate.extension

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory

@Category(Entity)
class StaticEntityExtension {

    Key makeKey(String param, String kind) {
        def id = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, id)
    }

    Entity make(String id, Class kind) {
        Key key = Entity.makeKey(id, kind.simpleName)
        new Entity(key)
    }


}
