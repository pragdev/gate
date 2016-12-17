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

    Map<String, String> queryParams() {
        // TODO querystring can be null
        if (queryString.trim().isEmpty()) return [:]

        queryString.split('&').inject([:]) { map, token ->
            token.split('=').with {
                map[it[0]] = it.size() > 1 ? it[1] : null
            }
            map
        }
    }

}