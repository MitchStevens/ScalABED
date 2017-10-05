package test

import core.circuit.{Game, Input, Output}
import core.types.Coord
import io.Reader
import main.scala.core.GameAction.AddAction
import org.scalatest.FlatSpec

import scala.collection.mutable.ArrayBuffer

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
    assert(add1.conn.isEmpty)
    assert(game.size == 1)
    assert(game.mesa_circuit.graphs.num_edges == 0)

    val add2 = game.add_evaluable(Reader.evaluable("BUS"), (1, 1)).get
    assert(add2.to == Coord(1, 1))
    assert(add2.name == "BUS")
    assert(add2.conn == ArrayBuffer(Coord(0, 1)))
    assert(game.size == 2)
    assert(game.mesa_circuit.graphs.num_edges == 1)

    val add3 = game.add_evaluable(new Output(1), (2, 1)).get
    assert(add3.to == Coord(2, 1))
    assert(add3.name == "Output")
    assert(add3.conn == ArrayBuffer(Coord(1 ,1)))
    assert(game.size == 3)
    assert(game.mesa_circuit.graphs.num_edges == 2)
  }

  it must "test 1" in {
    val game = new Game(5)

    val add1 = game.add_evaluable(Reader.evaluable("XOR"), (0, 0)).get
    assert(add1.name == "XOR")
    assert(add1.to == Coord(0, 0))
    assert(add1.conn.isEmpty)

    val add2 = game.add_evaluable(Reader.evaluable("TRUE"), (0, 1)).get
    assert(add2.name == "TRUE")
    assert(add2.to == Coord(0, 1))
    assert(add2.conn.isEmpty)

    val mov1 = game.move_evaluable((0, 0), (1, 1)).get
    assert(mov1.name == "XOR")
    assert(mov1.from == Coord(0, 0))
    assert(mov1.disconn.isEmpty)
    assert(mov1.to == Coord(1, 1))
    assert(mov1.conn == ArrayBuffer(Coord(0, 1)))

    val mov2 = game.move_evaluable((0, 1), (1, 0)).get
    assert(mov2.name == "TRUE")
    assert(mov2.from == Coord(0, 1))
    assert(mov2.disconn == ArrayBuffer(Coord(1, 1)))
    assert(mov2.to == Coord(1, 0))
    assert(mov2.conn.isEmpty)
  }

}
