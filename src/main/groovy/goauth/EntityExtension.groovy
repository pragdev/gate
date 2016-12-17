package goauth

import com.google.appengine.api.datastore.Entity

@Category(Entity)
class EntityExtension {

    void putAt(String property, Object newValue) {
       setProperty(property, newValue)
    }

}
