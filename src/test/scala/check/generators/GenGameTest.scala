package check.generators

import core.types.{GameTest, SimpleGameTest}
import org.scalacheck.{Arbitrary, Gen}

trait GenGameTest extends GenSignal {
  val gen_game_test: Gen[GameTest] =
    for {
      inputs  <- gen_signal
      outputs <- gen_signal
    } yield SimpleGameTest(inputs, outputs)

  implicit val arb_game_test: Arbitrary[GameTest] =
    Arbitrary(gen_game_test)
}
