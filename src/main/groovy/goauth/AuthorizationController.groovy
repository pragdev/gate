package goauth

import groovy.json.JsonBuilder
import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

@Log
public class AuthorizationController extends HttpServlet {

    Security security

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute 'security'
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            def params = request.queryParams()
            if (!params.client_id || !params.response_type) {
                response.status = SC_BAD_REQUEST
                return
            }
            log.info "Authentication requests: $params"

            response.setHeader 'Cache-Control', 'no-store'
            response.setHeader 'Pragma', 'no-cache'

            Client client = security.findClientBy params.client_id
            if (!client) {
                response.status = SC_UNAUTHORIZED
                return
            }

            def credentials = request.extractCredentialsFromHeader()
            def owner = security.identifyResourceOwnerBy credentials

            AccessRequest accessRequest = security.accessRequest(client, owner)

            response.contentType = 'application/json'
            response.status = SC_OK
            def builder = new JsonBuilder(accessRequest)
            response.writer << builder.toString()

        } catch (InvalidCredentialsException ex) {
            log.info("The credentials ($ex.credentials) are not valid")
            response.status = SC_UNAUTHORIZED
        }
    }

}
