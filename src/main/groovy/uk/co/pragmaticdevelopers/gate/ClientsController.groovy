package uk.co.pragmaticdevelopers.gate

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
class ClientsController extends HttpServlet {

    Security security

    @Override
    void init(ServletConfig config) throws ServletException {
        security = config.servletContext.getAttribute('security')
    }

    void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map json = new JsonSlurper().parse request.inputStream
        Client client = security.register new Client(
                name: json.name,
                redirectionUri: new URI(json.redirectionUri),
                type: json.type.toUpperCase()
        )

        JsonBuilder builder = new JsonBuilder()
        builder {
            id client.id
            secret client.secret
        }

        response.contentType = 'application/json'
        response.writer << builder.toString()
    }
}