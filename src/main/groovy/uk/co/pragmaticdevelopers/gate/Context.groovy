package uk.co.pragmaticdevelopers.gate

interface Context {
    Client findClientBy(id)
    Client findClient(Credentials credentials)
    ResourceOwner findResourceOwner(String username)
    ResourceOwner findResourceOwner(Credentials credentials)
    AccessRequest findAccessRequest(AccessRequest accessRequest)
    Token findToken(String tokenValue, Class<? extends Token> type)

    AccessToken makeAccessToken(Client client)
    AccessToken makeAccessToken(ResourceOwner owner)
}
