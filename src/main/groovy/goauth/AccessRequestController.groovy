package goauth

import groovy.json.JsonSlurper

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

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
            def json = slurper.parseText(request.inputStream.text)
            def accessRequest = new AccessRequest(id: json.id, status: AccessRequest.Status.valueOf(json.status))
            def accessToken = security.grantAccess accessRequest
            def redirectUri = security.redirectUriFor accessRequest.id

            response.status = SC_MOVED_TEMPORARILY
            response.setHeader('Location', "$redirectUri#access_token=${accessToken.value}&token_type=example&expires_in=3600")

        } catch (EntityNotFound ex) {
            response.status = SC_NOT_FOUND
        } catch (IllegalArgumentException | InvalidStatusException ex) {
            response.status = SC_BAD_REQUEST
        }
    }
}
