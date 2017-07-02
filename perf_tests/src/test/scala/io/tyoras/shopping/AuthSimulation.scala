package io.tyoras.shopping

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

class AuthSimulation extends ShoppingSimulation {
  val scn: ScenarioBuilder = scenario("Oauth2 token generation").repeat(nbTests)(
    exec(
      UserRegistration.registerUser,
      Auth.generateToken,
      User.delete
    )
  )
setUp(scn.inject(atOnceUsers(nbUsers)).protocols(httpConf))
}
