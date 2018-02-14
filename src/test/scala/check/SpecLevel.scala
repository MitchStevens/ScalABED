package check

import io.Level
import io.JsonInstances._
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}
import play.api.libs.json.{JsValue, Json}

object SpecLevel extends Properties("Level") with Generators {

  property("Level Parsing") = forAll { (l: Level) =>
    println(l)
    val json: JsValue = Json.toJson(l)
    println(json)
    l == json.as[Level]
  }
}
