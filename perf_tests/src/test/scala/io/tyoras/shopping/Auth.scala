package io.tyoras.shopping

import io.gatling.core.Predef._
import io.gatling.core.body.ElFileBody
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Auth {

  val generateToken: ChainBuilder = exec(
    http(s"Generate token")
      .post("/auth/token")
      .header("Accept", "application/json")
      .formParam("grant_type", "password")
      .formParam("username", "${email}")
      .formParam("password", "${password}")
      .formParam("client_id", appId)
      .formParam("client_secret", "not_required")
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("token"))
	)
}
