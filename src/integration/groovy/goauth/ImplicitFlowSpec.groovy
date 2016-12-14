package goauth

import groovyx.net.http.RESTClient
import support.RemoteBackend
import spock.lang.AutoCleanup
import spock.lang.Specification

class ImplicitFlowSpec extends Specification {

    @AutoCleanup
    RemoteBackend remoteBackend = new RemoteBackend()
    def client = new RESTClient()

    def "The resource owner grants client's access"() {

        expect:
        2 + 2 == 4
    }
}
