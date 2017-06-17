package io.tyoras.shopping

import io.gatling.core.Predef._
import io.gatling.core.body.ElFileBody
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object User extends UserFeeders {


  val delete: ChainBuilder = exec(
    http("Delete a user")
      .delete("/api/user/${user_id}")
      .header("Authorization", "bearer ${token}")
      .check(status.is(204))
  )
}
