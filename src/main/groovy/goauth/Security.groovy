package goauth

import groovy.util.logging.Log

enum AuthenticationFlow { CLIENT_CREDENTIALS, PASSWORD }

@Log
class Security {
  CredentialsRepository credentialsRepository
  ClientRepository clientsRepository

  AccessToken authenticate(Credentials credentials) throws InvalidCredentialsException {
    if (!credentialsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

    Credentials storedCredentials = credentialsRepository.find credentials.username
    if (storedCredentials != credentials) throw new InvalidCredentialsException(credentials)

    credentialsRepository.store new AccessToken()
  }

  Client register(Client client) {
    client.id = UUID.randomUUID().toString()
    client.secret = UUID.randomUUID().toString()

    clientsRepository.store client
  }

}
