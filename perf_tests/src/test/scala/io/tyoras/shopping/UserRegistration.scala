package io.tyoras.shopping

import io.gatling.core.Predef._
import io.gatling.core.body.ElFileBody
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object UserRegistration extends UserFeeders {

  val registerUser: ChainBuilder =
    feed(userName).feed(userEmail)
    .feed(randomPassword).feed(randomProfileVisibility)
    .exec(
      http("Register new user")
        .post("/public/user")
        .header("Accept", "application/json")
        .body(ElFileBody("user/registration.json")).asJson
        .check(status.is(201))
        .check(jsonPath("$..id").saveAs("user_id"))
	)
}
