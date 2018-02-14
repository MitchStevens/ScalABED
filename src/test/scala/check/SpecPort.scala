package check

/**
  * Created by Mitch on 7/11/2017.
  */
import core.circuit.Port
import io.JsonInstances.port_format
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}
import play.api.libs.json.{JsObject, JsValue, Json, Writes}

object SpecPort extends Properties("Port") with Generators {

  property("instantiation") = forAll { (p: Port) =>
    p != null
  }

  property("json parsing") = forAll { (p : Port) =>
    val json: JsValue = io.JsonInstances.port_format.writes(p)
    p == json.as[Port]
  }

}
