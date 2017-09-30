package test

import core.circuit.{Game, Input, Output}
import core.types.Coord
import io.Reader
import org.scalatest.FlatSpec

class TestGame extends FlatSpec {

  "A Game" must "instantiate correctly" in {
    val game: Game = new Game(5)
  }

  it must "add an evaluable" in {
    val game: Game = new Game(5)
    val add1 = game.add_evaluable(new Input(1), (0, 1)).get
    assert(add1.to == Coord(0, 1))
    assert(add1.name == "Input")
    assert(game.size == 1)
  }

  it must "simulate a bus correctly" in {
    val game: Game = new Game(5)

    val add1 = game.add_evaluable(new Input(1), (0, 1)).get
    assert(add1.to == Coord(0, 1))
    assert(add1.name == "Input")
    assert(game.size == 1)
    assert(game.mesa_circuit.graphs.num_edges == 0)

    val add2 = game.add_evaluable(Reader.evaluable("BUS"), (1, 1)).get
    assert(add2.to == Coord(1, 1))
    assert(add2.name == "BUS")
    assert(game.size == 2)
    assert(game.mesa_circuit.graphs.num_edges == 1)

    val add3 = game.add_evaluable(new Output(1), (2, 1)).get
    assert(add3.to == Coord(2, 1))
    assert(add3.name == "Output")
    assert(game.size == 3)
    assert(game.mesa_circuit.graphs.num_edges == 2)


  }

}
