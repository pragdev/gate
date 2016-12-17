package goauth.implicitgrant

import goauth.Required

class ImplicitGrantRequest {

    @Required String responseType
    @Required String clientId
    String redirectUri
    String scope
    String state

}
