package goauth

class ResourceOwner {
    Credentials credentials

    String getName() {
        credentials.username
    }
}
