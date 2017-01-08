package uk.co.pragmaticdevelopers.gate

import uk.co.pragmaticdevelopers.gate.httpconverter.AccessTokenRequestConverter
import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.presenter.Presenter

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
        try {
            def tokenRequest = converter.convert request
            def credentials = request.extractCredentialsFromBody()
            if (!credentials) credentials = request.extractCredentialsFromHeader()

            def token = security.issueAccessToken(tokenRequest, credentials)

            response.setHeader('Cache-Control', 'no-store')
            response.setHeader('Pragma', 'no-cache')

            response.contentType = 'application/json'
            response.writer << presenter.present(token)
        } catch (MissingQueryParamException ex) {
            response.sendError(SC_BAD_REQUEST, 'missing required param')
        }
    }

}
