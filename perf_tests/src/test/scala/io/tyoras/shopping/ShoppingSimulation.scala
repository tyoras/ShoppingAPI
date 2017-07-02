package io.tyoras.shopping

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

abstract class ShoppingSimulation extends Simulation {
  val httpConf: HttpProtocolBuilder = http.baseURL(baseUrl)
  val nbUsers: Int = nbUsersAtOnce
  var nbTests: Int = nbRepeat
}
