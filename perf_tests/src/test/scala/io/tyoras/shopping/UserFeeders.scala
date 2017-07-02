package io.tyoras.shopping

import scala.util.Random

trait UserFeeders {
  val rnd = new Random

  def getRandomElement(list: Seq[String], random: Random): String =
    list(random.nextInt(list.length))

  val userName: Iterator[Map[String, String]] = Iterator.from(1).map(i => Map("name" -> s"Gatling User #$i"))

  val userEmail: Iterator[Map[String, String]] = Iterator.from(1).map(i => Map("email" -> s"gatling_user_$i@shopping-app.io"))

  val randomPassword = Iterator.continually(Map("password" -> (Random.alphanumeric.take(20).mkString)))

  val profileVisibilities = Seq("PUBLIC", "PRIVATE")
  val randomProfileVisibility: Iterator[Map[String, String]] =
    Iterator.continually(Map("profileVisibility" -> getRandomElement(profileVisibilities, rnd)))
}
