package uk.co.pragmaticdevelopers.gate.extensions

import com.google.appengine.api.datastore.Entity

@Category(Entity)
class EntityExtension {

    void putAt(String property, Object newValue) {
       setProperty(property, newValue)
    }

    def getAt(String property) {
        getProperty(property)
    }

}
