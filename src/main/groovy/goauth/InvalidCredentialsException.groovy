package goauth

class InvalidCredentialsException extends Exception {
  Credentials credentials

  InvalidCredentialsException(Credentials credentials) {
    super("Invalid username (${credentials?.username}) or Password (${credentials?.password})")
    this.credentials = credentials
  }
}
