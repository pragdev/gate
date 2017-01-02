package goauth.implicitgrant

import goauth.AccessRequest
import goauth.Credentials
import goauth.Security

class ImplicitGrantFlow {

    Security security

    def accessRequest(ImplicitGrantRequest grantRequest, Credentials credentials) {
        security.issueAccessRequest(grantRequest, credentials)
    }

    def grantAccess(AccessRequest accessRequest) {
        security.grantAccess(accessRequest)
    }

    def denyAccess() {}

}
