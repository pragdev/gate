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

  CredentialsRepository credentialsRepository

  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    def parsed = new JsonSlurper().parse(request.inputStream)
    credentialsRepository.store new Credentials(parsed.username, parsed.password)
  }

  @Override
  void init(ServletConfig config) throws ServletException {
    credentialsRepository = new CredentialsRepository()
  }
}