package uk.co.pragmaticdevelopers.gate

interface OAuthEvents {
    void onAccessTokenIssued(AccessToken token, Credentials credentials)
    void onNewClientRegistered(Client client)
    void onNewResourceOwner(ResourceOwner owner, AccessToken token)
    void onNewAccessRequest(AccessRequest accessRequest)
    void onGrantedAccess(storedAccessRequest)
}
