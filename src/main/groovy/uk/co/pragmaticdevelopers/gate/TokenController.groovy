package uk.co.pragmaticdevelopers.gate

import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.httpconverter.AccessTokenRequestConverter
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
            if (!credentials) credentials = request.extractBasicCredentials()

            def token = security.issueAccessToken(tokenRequest, credentials)

            response.noCache()
            response.sendJson(token, presenter)
        } catch (MissingQueryParamException ex) {
            response.sendError(SC_BAD_REQUEST, 'missing required param')
        }
    }

}
