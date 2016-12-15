package goauth

import groovy.json.JsonSlurper
import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
class RemoteController extends HttpServlet {

    ResourceOwnerRepository resourceOwnerRepository

    void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        def parsed = new JsonSlurper().parse(request.inputStream)
        resourceOwnerRepository.store new ResourceOwner(parsed.username, parsed.password, parsed.displayName)
    }

    @Override
    void init(ServletConfig config) throws ServletException {
        resourceOwnerRepository = new ResourceOwnerRepository()
    }
}