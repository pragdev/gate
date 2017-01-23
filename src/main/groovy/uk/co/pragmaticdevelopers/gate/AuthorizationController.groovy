package uk.co.pragmaticdevelopers.gate

import groovy.json.JsonBuilder
import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.httpconverter.GrantConverter
import uk.co.pragmaticdevelopers.gate.presenter.Presenter

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

@Log
public class AuthorizationController extends HttpServlet {

    Security security
    GrantConverter converter
    Presenter presenter

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute 'security'
        converter = config.servletContext.getAttribute 'grantConverter'
        presenter = config.servletContext.getAttribute 'presenter'

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            def grantRequest = converter.convert request
            def credentials = request.extractBasicCredentials()

            AccessRequest accessRequest = security.issueAccessRequest(grantRequest, credentials)

            response.asJson()
            response.status = SC_OK
            response.noCache()
            response.sendJson(accessRequest, presenter)

        } catch (InvalidCredentialsException ex) {
            log.info "The credentials ($ex.credentials) are not valid"
            response.status = SC_UNAUTHORIZED
        } catch (MissingQueryParamException ex) {
            log.info 'Missing params in request'
            response.status = SC_BAD_REQUEST
        } catch (EntityNotFound ex) {
            log.info 'client not found'
            response.status = SC_UNAUTHORIZED
        }
    }
}
