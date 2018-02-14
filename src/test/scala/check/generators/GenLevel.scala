package check.generators

import core.circuit.Port
import core.types.GameTest
import io.{Level, LevelSet}
import org.scalacheck.{Arbitrary, Gen}

trait GenLevel extends GenPort with GenGameTest {
  val gen_level: Gen[Level] =
    for {
      name         <- Gen.alphaNumStr
      optimal_size <- Gen.choose(3, 10)
      instructions <- Gen.alphaNumStr
      completion   <- Gen.alphaNumStr
      ports        <- Gen.listOfN(4, Arbitrary.arbitrary[Port]).map(_.toArray)
      tests        <- Gen.listOf(Arbitrary.arbitrary[GameTest])
    } yield Level (name, optimal_size, instructions, completion, ports, tests)

  val gen_level_set: Gen[LevelSet] =
    for {
      name <- Gen.alphaNumStr
      levels <- Gen.listOf(gen_level)
    } yield LevelSet (name, levels)

  implicit val arb_level: Arbitrary[Level] = Arbitrary(gen_level)
  implicit val arb_level_set: Arbitrary[LevelSet] = Arbitrary(gen_level_set)
}
