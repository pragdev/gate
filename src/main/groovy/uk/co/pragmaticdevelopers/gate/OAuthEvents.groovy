package uk.co.pragmaticdevelopers.gate

interface OAuthEvents {
    void onAccessTokenIssued(AccessToken token, Credentials credentials)
    void onNewClientRegistered(Client client)
    AccessToken onNewResourceOwner(ResourceOwner owner)
    void onNewAccessRequest(AccessRequest accessRequest)
    void onGrantedAccess(storedAccessRequest)
}
