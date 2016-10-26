package goauth

import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent

class ContextListenerSpec extends Specification {

  def "should add the security to the context"() {
    given:
    def listener = new ContextListener()
    ServletContext context = Mock()
    ServletContextEvent event = Mock() {
      getServletContext() >> context
    }

    when:
    listener.contextInitialized(event)

    then:
    1 * context.setAttribute('security', _ as Security)
  }
}
