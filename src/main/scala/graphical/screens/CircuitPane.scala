package main.scala.graphical.screens

import core.circuit.{Evaluable, Game}
import core.types.{Coord, Direction}
import graphical.screens.CircuitPaneTransitions
import io.Reader
import main.scala.core.GameAction._
import main.scala.graphical.Main
import main.scala.graphical.controls.{Piece, Square}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
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
    (math.min(this.height.value, this.width.value) - 2 * MARGIN) / num_tiles.value
  }, this.height, this.width, num_tiles)
  val tile_originX = (this.width - num_tiles * tile_size) * 0.5
  val tile_originY = (this.height - num_tiles * tile_size) * 0.5
  var current_game = new Game(5)


  val repaint_loop = Future {
    while (true)
      if (repainting) {
        for (pos <- pieces.keys) {
          val d = current_game.coord_map(pos)
          val state = current_game.state.get_state(d.id)
          pieces(pos) repaint (d.rotation, state)
        }
        Thread.sleep(1000 / FPS)
      }
  }

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

  def add(e: Evaluable, pos: Coord): Option[AddAction] = {
    val action = current_game.add_evaluable(e, pos)
    if (action.isDefined) {
      val piece = new Piece(e, pos)
      this.children.add(piece)
      pieces += pos -> piece
      CircuitPaneTransitions.add(piece).play()
    }
    action
  }

  def add_somewhere(e: Evaluable): Option[AddAction] =
    current_game.first_open flatMap (c => this.add(e, c))

  def remove(pos: Coord): Option[RemoveAction] = {
    for (action <- current_game.remove_evaluable(pos); piece <- pieces.get(pos)) {
      val transition = CircuitPaneTransitions.remove(piece)
      transition.onFinished = _ => {this.children.remove(piece)}
      transition.play()
      pieces -= pos
      return Some(action)
    }
    None
  }

  def move(from: Coord, to: Coord): Option[MoveAction] = {
    for (action <- current_game.move_evaluable(from, to); piece <- pieces.get(from); sq <- squares.get(to)) {
      pieces += to -> piece
      pieces -= from
      piece.move(to)
      CircuitPaneTransitions.move(piece, sq).play()
      return Some(action)
    }
    None
  }

  def rotate(pos: Coord, rot: Direction): Option[RotateAction] = {
    current_game.rotate_evaluable(pos, rot)
  }

  def toggle(pos: Coord, a: Any): Option[ToggleAction] = {
    for (action <- current_game.toggle_evaluable(pos, a); sq <- squares.get(pos)) {
      CircuitPaneTransitions.toggle(sq.translateX(), sq.translateY()).play()
      return Some(action)
    }
    None
  }

  /*private def rotate_transition(node: Node, rot: Direction): Transition =
    new RotateTransition(Duration.Zero, node) {
      byAngle = 90.0 * rot
    }*/
}