package main.scala.graphical.screens

import core.circuit.Game
import core.types.{Coord, Direction}
import main.scala.graphical.Main
import main.scala.graphical.controls.{Piece, Square}

import scala.collection.mutable
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.layout.GridPane

/**
  * Created by Mitch on 7/31/2017.
  */
object CircuitPane extends GridPane {
  private val SPACING = 70.0
  private val FPS: Int = 24

  private var repainting: Boolean = true

  val squares = mutable.Map.empty[Coord, Square]
  val pieces = mutable.Map.empty[Coord, Piece]
  val tile_size = DoubleProperty(10)
  val num_tiles = IntegerProperty(0)
  var current_game = new Game(5)

  /*
  val repaint_loop = Future {
    while (true)
      if (repainting)
        pieces.values foreach (_.repaint())
      Thread.sleep(1000/FPS)
  }
  */
  id = "circuit_pane"
  styleClass = Seq("bordered")
  stylesheets = Seq(
    "@../../css/all-panes.css",
    "@../../css/circuit-pane.css"
  )
  tile_size <== createDoubleBinding(
    () => (scala.math.min(Main.board_width.value - Sidebar.WIDTH, Main.board_height.value) - (2 * SPACING)) / num_tiles.toDouble,
    Main.board_width, Main.board_height, this.num_tiles
  )
  num_tiles onChange {
    (_, old_value, new_value) => {
      val o = old_value.intValue()
      val n = new_value.intValue()
      if (n > o) this.increase_size(o, n)
      else if (n < o) this.decrease_size(o, n)
    }
  }
  num_tiles <== Sidebar.incrementer.value

  private def increase_size(o: Int, n: Int): Unit =
    for (c <- Coord.over_square_rem(o, n)) {
      val square = new Square(c)
      this.squares += c -> square
      this.add(square, c.y, c.x)
    }

  private def decrease_size(o: Int, n: Int): Unit = {
      for {
        c <- Coord.over_square_rem(n, o)
        s <- squares.remove(c)
      } this.children -= s
    }

}
