package goauth.flows.implicit

import goauth.AccessRequest
import goauth.AccessToken
import groovy.transform.Canonical

@Canonical
class ImplicitFlowAccessRequest extends AccessRequest {

    @Override
    AccessToken makeToken() {
        new AccessToken()
    }
}