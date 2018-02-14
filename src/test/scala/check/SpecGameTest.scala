package check

import core.types.{GameTest, SimpleGameTest}
import io.JsonInstances._
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

import generators.GenGameTest

object SpecGameTest extends Properties("GameTest") with GenGameTest {

  property("json parsing") = forAll { (test: GameTest) =>
    val json = io.JsonInstances.test_format.writes(test)
    test == json.as[GameTest]
  }

}
