package goauth

import javax.servlet.http.HttpServletRequest

@Category(HttpServletRequest)
class HttpServletRequestExtension {

    Credentials extractCredentialsFromBody() {
        def username = this.getParameter('username')
        def password = this.getParameter('password')

        !username || !password ? null : new Credentials(username, password)
    }

    Credentials extractCredentialsFromHeader() {
        def header = getHeader('Authorization')?.minus 'Basic '
        if (!header) return null

        def decoded = new String(header.decodeBase64())
        def (username, password) = decoded.tokenize(':')

        !username || !password ? null : new Credentials(username, password)
    }

}