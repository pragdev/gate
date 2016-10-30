package goauth

import groovy.util.logging.Log

@Log
class Security {

  CredentialsRepository credentialsRepository
  private Authenticator passwordFlow = { Credentials credentials ->
    if (!credentialsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

    Credentials storedCredentials = credentialsRepository.find credentials.username
    if (storedCredentials != credentials) throw new InvalidCredentialsException(credentials)

    credentialsRepository.store new AccessToken()
  }

  private Authenticator clientCredentialsFlow = { Credentials credentials ->
    if (!credentialsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

    Credentials storedCredentials = credentialsRepository.find credentials.username
    if (storedCredentials != credentials) throw new InvalidCredentialsException(credentials)

    credentialsRepository.store new AccessToken()
  }

  def store(Credentials credentials) {
    log.info "credentials ${credentials.username} ${credentials.password}"
  }

  AccessToken authenticate(Map options = null, Credentials credentials) {
    if(options.flow == 'password') {
      passwordFlow.authenticate credentials
    } else if(options.flow == 'client_credentials') {
      clientCredentialsFlow.authenticate options.clientCredentials
    }
  }

}

interface Authenticator {
  AccessToken authenticate(Credentials credentials) throws InvalidCredentialsException
}
