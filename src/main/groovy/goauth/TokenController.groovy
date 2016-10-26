package goauth

import com.google.appengine.api.datastore.DatastoreServiceFactory
import groovy.json.JsonBuilder
import groovy.util.logging.Log

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
public class TokenController extends HttpServlet {

  Security security

  @Override
  void init(ServletConfig config) throws ServletException {
    super.init(config)

    security = config.servletContext.getAttribute('security')
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    def credentials = new Credentials(request.getParameter('username'), request.getParameter('password'))
    def token = security.authenticate credentials

    response.setHeader('Cache-Control', 'no-store')
    response.setHeader('Pragma', 'no-cache')

    response.setContentType('application/json')

    def builder = new JsonBuilder()
    builder {
      access_token token.value
      token_type 'bearer'
      expires_in token.expiresIn
    }
    response.writer << builder.toString()
  }

}
