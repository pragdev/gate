package uk.co.pragmaticdevelopers.gate

abstract class GrantRequest {
    @Required String responseType
    @Required String clientId
    String redirectUri
    String scope
    String state

    GrantRequest() {
        super()
        type()
    }

    protected abstract void type()

    abstract AccessRequest makeAccessRequest(Map args)

    abstract boolean isValidType()
}
