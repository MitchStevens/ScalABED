package io

import core.circuit.Port
import core.types.{GameTest, SimpleGameTest}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._ // Combinator syntax

object JsonInstances {

  implicit val level_format: Format[Level] = (
    (JsPath \ "name").format[String]           and
    (JsPath \ "optimal_size").format[Int]      and
    (JsPath \ "instructions").format[String]   and
    (JsPath \ "completion").format[String]     and
    (JsPath \ "io").format[Array[Port]]        and
    (JsPath \ "tests").format[Seq[GameTest]]
  )(Level.apply, unlift(Level.unapply))

  implicit val test_format: Format[GameTest] = new Format[GameTest] {
    override def reads(json: JsValue): JsResult[GameTest] = json.validate[SimpleGameTest]

    override def writes(test: GameTest): JsValue = test match {
      case simple: SimpleGameTest => simple_game_test_format.writes(simple)
      case _ => throw new Exception("couldn't write ")
    }

    implicit val simple_game_test_format: Format[SimpleGameTest] = (
      (JsPath \ "ins").format[List[Int]]   and
      (JsPath \ "outs").format[List[Int]]
    ) (SimpleGameTest.apply, unlift(SimpleGameTest.unapply))
  }

  implicit val port_format: Format[Port] = new Format[Port] {

    override def reads(json: JsValue): JsResult[Port] =
      for (_type <- (json \ "type").validate[String].map(Port.PortType.withName))
        yield _type match {
          case Port.PortType.UNUSED => Port.unused
          case other_type           =>
            val capacity = (json \ "capacity").asOpt[Int].getOrElse(0)
            Port(other_type, capacity)
        }

    override def writes(port: Port): JsValue =
      if (port.is_unused)
        JsObject(Seq("type" -> JsString("UNUSED")))
      else JsObject(Seq(
        "type"     -> JsString(port.port_type.toString),
        "capacity" -> JsNumber(port.capacity)
      ))

  }

  implicit val reward_format: Format[Reward] = new Format[Reward] {
    override def reads(json: JsValue): JsResult[Reward] =
      for {
        _type <- (json \ "type").validate[String]
        name  <- (json \ "name").validate[String]
      } yield _type match {
        case "circuit"  => CircuitReward(name)
        case "level"    => LevelReward(name)
      }

    override def writes(reward: Reward): JsValue = reward match {
      case CircuitReward(name) => JsObject(Seq(
        "type" -> JsString("circuit"),
        "name" -> JsString(name)
      ))
      case LevelReward(name) => JsObject(Seq(
        "type" -> JsString("level"),
        "name" -> JsString(name)
      ))
    }
  }

  implicit val level_set_format: Format[LevelSet] = (
    (JsPath \ "level_set").format[String] and
    (JsPath \ "levels").format[Seq[Level]]
  ) (LevelSet.apply, unlift(LevelSet.unapply))

}
