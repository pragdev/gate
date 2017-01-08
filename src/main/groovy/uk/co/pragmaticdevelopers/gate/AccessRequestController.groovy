package uk.co.pragmaticdevelopers.gate

import groovy.json.JsonSlurper
import uk.co.pragmaticdevelopers.gate.flow.implicit.ImplicitFlowAccessRequest

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

public class AccessRequestController extends HttpServlet {

    Security security
    JsonSlurper slurper = new JsonSlurper()

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)
        security = config.servletContext.getAttribute 'security'
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // TODO test invalid json
            Map json = slurper.parse(request.inputStream)
            // TODO introduce factory
            def accessRequest = new ImplicitFlowAccessRequest(id: json.id, status: AccessRequest.Status.valueOf(json.status))
            def token = security.grantAccess accessRequest
            URI redirectUri = security.redirectUriFor accessRequest
            def (tokenName, tokenValue) = token.describe()

            response.status = SC_MOVED_TEMPORARILY
            response['Location'] = "$redirectUri#${tokenName}=${tokenValue}&token_type=example&expires_in=3600"

        } catch (EntityNotFound ex) {
            response.status = SC_NOT_FOUND
        } catch (IllegalArgumentException | InvalidStatusException ex) {
            response.status = SC_BAD_REQUEST
        }
    }
}
