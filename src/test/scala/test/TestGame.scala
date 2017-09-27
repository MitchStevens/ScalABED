package test

import core.circuit.{Game, Input}
import org.scalatest.FlatSpec

class TestGame extends FlatSpec {

  "A Game" must "instantiate correcly" in {
    val game: Game = new Game(5)
  }

  it must "add an evaluable" in {
    val game: Game = new Game(5)
    game.add(new Input(1), (0, 1))
    )
  }

}
