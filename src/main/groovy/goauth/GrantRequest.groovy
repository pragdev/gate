package goauth

abstract class GrantRequest {
    @Required String responseType
    @Required String clientId
    String redirectUri
    String scope
    String state

    GrantRequest(Map args) {
        super(args)
        type()
    }

    GrantRequest() {
        super()
        type()
    }

    protected abstract void type()

    protected abstract AccessRequest makeAccessRequest(Map args)

    abstract boolean isValidType()
}
