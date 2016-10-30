package goauth

import groovy.util.logging.Log

enum AuthenticationFlow { CLIENT_CREDENTIALS, PASSWORD }

@Log
class Security {
  private CredentialsRepository credentialsRepository

  Security(CredentialsRepository credentialsRepository) {
    this.credentialsRepository = credentialsRepository
  }

  AccessToken authenticate(Credentials credentials) throws InvalidCredentialsException {
    if (!credentialsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

    Credentials storedCredentials = credentialsRepository.find credentials.username
    if (storedCredentials != credentials) throw new InvalidCredentialsException(credentials)

    credentialsRepository.store new AccessToken()
  }

}
