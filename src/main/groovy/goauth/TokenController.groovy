package goauth

import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static goauth.AuthenticationFlow.PASSWORD
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Log
public class TokenController extends HttpServlet {

    Security security
    Presenter presenter
    AccessTokenRequestConverter converter

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute 'security'
        presenter = config.servletContext.getAttribute 'presenter'
        converter = config.servletContext.getAttribute 'accessTokenRequestConverter'
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        def tokenRequest = converter.convert request
        def credentials = request.extractCredentialsFromBody()

        def token = security.issueAccessToken(tokenRequest, credentials)

        response.setHeader('Cache-Control', 'no-store')
        response.setHeader('Pragma', 'no-cache')

        response.contentType = 'application/json'
        response.writer << presenter.present(token)
    }

}
