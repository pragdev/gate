package goauth

import groovy.util.logging.Log

@Log
class Security {

  CredentialsRepository credentialsRepository

  def store(Credentials credentials) {
    log.info "credentials ${credentials.username} ${credentials.password}"
  }

  AccessToken authenticate(Credentials credentials) {
    if( !credentialsRepository.exists(credentials.username) ) throw new InvalidCredentialsException()

    Credentials storedCredentials = credentialsRepository.find credentials.username
    if(storedCredentials != credentials) throw new InvalidCredentialsException()

    credentialsRepository.store new AccessToken()
  }
}
