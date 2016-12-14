package goauth

import groovy.json.JsonBuilder
import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
public class TokenController extends HttpServlet {

    Security security
    private Map<AuthenticationFlow, Closure> credentialsExtractors

    TokenController() {
        this.credentialsExtractors = [
                (AuthenticationFlow.CLIENT_CREDENTIALS): this.&extractCredentialsFromHeader,
                (AuthenticationFlow.PASSWORD)          : this.&extractCredentialsFromBody
        ]
    }

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute('security')
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuthenticationFlow flow = request.getParameter('grant_type').toUpperCase()
        def credentials = credentialsExtractors[flow](request)

        def token = security.authenticate credentials

        response.setHeader('Cache-Control', 'no-store')
        response.setHeader('Pragma', 'no-cache')

        response.contentType = 'application/json'

        def builder = new JsonBuilder()
        builder {
            access_token token.value
            token_type 'bearer'
            expires_in token.expiresIn
        }
        response.writer << builder.toString()
    }

    Credentials extractCredentialsFromBody(HttpServletRequest request) {
        def username = request.getParameter('username')
        def password = request.getParameter('password')

        !username || !password ? null : new Credentials(username, password)
    }

    Credentials extractCredentialsFromHeader(HttpServletRequest request) {
        def header = request.getHeader('Authorization')?.minus 'Basic '
        if (!header) return null

        def decoded = new String(header.decodeBase64())
        def (username, password) = decoded.tokenize(':')

        !username || !password ? null : new Credentials(username, password)
    }
}
