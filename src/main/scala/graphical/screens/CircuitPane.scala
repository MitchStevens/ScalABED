package main.scala.graphical.screens

import akka.actor.{ActorRef, ActorSystem}
import akka.dispatch.{MailboxType, MessageQueue}
import core.{Logger, Paintable}
import core.circuit.{CircuitGraph, CircuitState, Evaluable, Game}
import core.types.{Coord, Direction}
import graphical.screens.CircuitPaneTransitions
import io.Reader
import main.scala.core.GameAction
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
object CircuitPane extends Pane with Paintable[CircuitState] {
  private val FPS: Int = 5
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
  var current_game = new Game(0)

  Logger.add_handler {
    case _: PieceAction => repaint(current_game.state)
  }

  override def repaint(t: CircuitState): Unit = {
    println("repainted: "+ pieces.keys)
    for (pos <- pieces.keys) {
      val d = current_game.coord_map(pos)
      val state = t.get_state(d.id)
      pieces(pos) repaint (d.rotation, state)
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
  Sidebar.incrementer.value onChange {(_, _, n) => num_tiles set n.intValue()}

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

  def set_game(game: Game): Unit = {
    current_game = game
    num_tiles set game.board_size
    pieces.clear()

    for (pos <- game.coord_map.keys) {
      val p = new Piece(game(pos), pos)
      pieces += pos -> p
      this.children += p
    }
  }

  def add(e: Evaluable, pos: Coord): Unit =
    for (action <- current_game.add_evaluable(e, pos)){
      val piece = new Piece(e, pos)
      this.children.add(piece)
      pieces += pos -> piece
      CircuitPaneTransitions.add(piece).play()
      action.send()
    }

  def add_somewhere(e: Evaluable): Unit =
    current_game.first_open foreach (c => this.add(e, c))

  def remove(pos: Coord): Unit =
    for (action <- current_game.remove_evaluable(pos); piece <- pieces.get(pos)) {
      val transition = CircuitPaneTransitions.remove(piece)
      transition.onFinished = _ => {this.children.remove(piece)}
      transition.play()
      pieces -= pos
      action.send()
    }

  def move(from: Coord, to: Coord): Unit =
    for (action <- current_game.move_evaluable(from, to); piece <- pieces.get(from); sq <- squares.get(to)) {
      pieces += to -> piece
      pieces -= from
      piece.move(to)
      CircuitPaneTransitions.move(piece, sq).play()
      action.send()
    }

  def rotate(pos: Coord, rot: Direction): Unit =
    for (action <- current_game.rotate_evaluable(pos, rot))
      action.send()

  def toggle(pos: Coord, a: Any): Unit =
    for (action <- current_game.toggle_evaluable(pos, a); sq <- squares.get(pos)) {
      CircuitPaneTransitions.toggle(sq.translateX(), sq.translateY()).play()
      action.send
    }
}