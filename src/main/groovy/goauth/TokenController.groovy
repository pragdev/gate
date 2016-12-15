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
    Presenter presenter

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute 'security'
        presenter = config.servletContext.getAttribute 'presenter'
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuthenticationFlow flow = request.getParameter('grant_type').toUpperCase()
        def credentials = extractCredentialsFromBody(request)

        def token = security.authenticateResourceOwner credentials

        response.setHeader('Cache-Control', 'no-store')
        response.setHeader('Pragma', 'no-cache')

        response.contentType = 'application/json'
        response.writer << presenter.present(token)
    }

    Credentials extractCredentialsFromBody(HttpServletRequest request) {
        def username = request.getParameter('username')
        def password = request.getParameter('password')

        !username || !password ? null : new Credentials(username, password)
    }

}
