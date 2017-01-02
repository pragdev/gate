package goauth

import groovy.transform.Canonical

@Canonical
class ImplicitFlowAccessRequest extends AccessRequest {

    @Override
    AccessToken makeToken() {
        new AccessToken()
    }
}