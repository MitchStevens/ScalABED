package test.parsing

import org.scalatest.FlatSpec
import io.JsonInstances._
import io.Level
import play.api.libs.json.Json

class json_parsing extends FlatSpec {

  "A Port" must "be parsed correctly" in {

  }

  "dsadsa" must "fdsafsdfadsa" in {
    val json = Json.parse(
      """
        |{ "name":"Introduction to BabyTown",
        |        "optimal_size":3,
        |        "io": [
        |          {"type":"UNUSED"},
        |          {"type":"OUTPUT", "capacity":1},
        |          {"type":"UNUSED"},
        |          {"type":"INPUT", "capacity":1}
        |        ],
        |        "instructions":"Place an input on the left and an output on the right. Then connect them by using bus pieces.",
        |        "tests":[
        |          {"ins":[0], "outs":[0]},
        |          {"ins":[1], "outs":[1]}
        |        ],
        |        "completion":"completion_text388383",
        |        "rewards":[
        |          {"type":"circuit", "name":"NOT"},
        |          {"type":"level", "name":"0.1"}
        |        ]
        |      }
      """.stripMargin)
    val level = json.as[Level]
  }

}
