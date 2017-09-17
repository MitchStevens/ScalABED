package main.scala.graphical.screens

import core.circuit.{Evaluable, Game}
import core.types.{Coord, Direction}
import main.scala.core.GameAction._
import main.scala.graphical.Main
import main.scala.graphical.controls.{Piece, Square}

import scala.collection.mutable
import scalafx.Includes._
import scalafx.beans.binding.{Bindings, NumberBinding}
import scalafx.beans.property._
import scalafx.scene.layout.{GridPane, Pane}

/**
  * Created by Mitch on 7/31/2017.
  */
object CircuitPane extends Pane {
  private val FPS: Int = 24
  private val MARGIN: Double = 5.0

  private var repainting: Boolean = true

  val squares = mutable.Map.empty[Coord, Square]
  val pieces = mutable.Map.empty[Coord, Piece]

  prefWidth  <== GamePane.inner_pane_width - Sidebar.WIDTH - GamePane.PADDING
  prefHeight <== GamePane.inner_pane_height
  translateX <== GamePane.SPACING
  translateY <== GamePane.SPACING
  val num_tiles = IntegerProperty(0)
  val tile_size: NumberBinding = Bindings.createDoubleBinding(() => {
    (math.min(this.height.value, this.width.value) - 2*MARGIN) / num_tiles.value
  }, this.height, this.width, num_tiles)
  val tile_originX = (this.width  - num_tiles * tile_size) * 0.5
  val tile_originY = (this.height - num_tiles * tile_size) * 0.5
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
      this.children += square
    }

  private def decrease_size(o: Int, n: Int): Unit = {
      for {
        c <- Coord.over_square_rem(n, o)
        s <- squares.remove(c)
      } this.children -= s
    }
  /*
  def add(e: Evaluable, pos: Coord): Option[AddAction] =
  current_game.add(e, pos) map ((action: AddAction) => {
    val piece = new Piece(e, pos)
    this.children.add(piece)
    pieces += pos -> piece
    action
  })

  def remove(pos: Coord): Option[RemoveAction] =
    current_game.remove(pos) map ((action: RemoveAction) => {
      val piece = pieces.remove(pos)
      piece foreach this.children.remove
      action
    })

  def move(from: Coord, to: Coord): Option[MoveAction] =
    current_game.move(from, to) map ((action: MoveAction) => {
      val piece = pieces.remove(from)

      //piece foreach (pieces += (to, _))
      action
    })
  */
}
