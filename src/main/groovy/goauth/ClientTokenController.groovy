package goauth

import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static goauth.AuthenticationFlow.CLIENT_CREDENTIALS
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Log
public class ClientTokenController extends HttpServlet {

    Security security
    Presenter presenter

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute('security')
        presenter = config.servletContext.getAttribute('presenter')
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuthenticationFlow flow = request.getParameter('grant_type').toUpperCase()
        if(flow != CLIENT_CREDENTIALS) {
            response.status = SC_BAD_REQUEST
            return
        }

        def credentials = request.extractCredentialsFromHeader()
        def token = security.authenticateClient credentials

        response.setHeader('Cache-Control', 'no-store')
        response.setHeader('Pragma', 'no-cache')

        response.contentType = 'application/json'
        response.writer << presenter.present(token)
    }

}
