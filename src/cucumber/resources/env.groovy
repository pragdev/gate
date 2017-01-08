import com.google.appengine.tools.remoteapi.RemoteApiInstaller
import cucumber.api.Scenario
import uk.co.pragmaticdevelopers.gate.MyWorld

import static cucumber.api.groovy.Hooks.*

RemoteApiInstaller installer = new RemoteApiInstaller()
installer.install MyWorld.remoteOptions


World {
    new MyWorld()
}

Before { Scenario scenario ->

}

After { Scenario scenario ->

}