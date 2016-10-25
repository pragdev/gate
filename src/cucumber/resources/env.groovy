import cucumber.api.Scenario
import goauth.MyWorld

import static cucumber.api.groovy.Hooks.*

World {
  new MyWorld()
}

Before { Scenario scenario ->

}

After { Scenario scenario ->

}