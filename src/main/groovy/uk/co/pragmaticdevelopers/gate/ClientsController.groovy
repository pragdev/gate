package uk.co.pragmaticdevelopers.gate

import groovy.json.JsonSlurper
import groovy.util.logging.Log
import uk.co.pragmaticdevelopers.gate.presenter.Presenter

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
class ClientsController extends HttpServlet {

    Security security
    Presenter presenter

    @Override
    void init(ServletConfig config) throws ServletException {
        security = config.servletContext.getAttribute('security')
        presenter = config.servletContext.getAttribute('presenter')
    }

    void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map json = new JsonSlurper().parse request.inputStream
        Client client = security.register new Client(
                name: json.name,
                redirectionUri: new URI(json.redirectionUri),
                type: json.type.toUpperCase()
        )

        response.sendJson(client, presenter)
    }
}