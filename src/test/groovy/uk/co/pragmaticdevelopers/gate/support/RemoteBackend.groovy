package uk.co.pragmaticdevelopers.gate.support

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.tools.remoteapi.RemoteApiInstaller
import com.google.appengine.tools.remoteapi.RemoteApiOptions
import uk.co.pragmaticdevelopers.gate.ResourceOwnerRepository

class RemoteBackend {

    DatastoreService datastore
    ResourceOwnerRepository resourceOwnerRepository

    RemoteBackend() {
        RemoteApiInstaller installer = new RemoteApiInstaller()
        installer.install new RemoteApiOptions().server('localhost', 8080).useDevelopmentServerCredential()

        datastore = DatastoreServiceFactory.datastoreService
        resourceOwnerRepository = new ResourceOwnerRepository(datastore: datastore)
    }

    def clean() {
        def keys = datastore.prepare new Query().setKeysOnly() asIterable() collect { it.key }
        datastore.delete keys
    }

    def close() {
        clean()
    }

}
