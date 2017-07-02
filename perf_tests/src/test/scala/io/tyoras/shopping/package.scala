package io.tyoras

import com.typesafe.config.{Config, ConfigFactory}

import scala.io.Source

package object shopping {
  lazy val env: String = scala.sys.props.get("load.env").getOrElse("local")
  lazy val config: Config = ConfigFactory.load("application.conf")
  lazy val baseUrl: String = config.getString(s"$env.baseUrl")
  lazy val appId: String = config.getString(s"$env.appId")
  lazy val nbRepeat: Int = config.getInt(s"$env.nbTests")
  lazy val nbUsersAtOnce: Int = config.getInt(s"$env.nbUsers")
}
