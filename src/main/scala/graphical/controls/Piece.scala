package main.scala.graphical.controls

import core.circuit.{Evaluable, Port}
import core.types.Direction
import main.scala.graphical.screens.CircuitPane
import Piece._
import io.Reader

import scalafx.beans.binding._
import scalafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Pane}
import scalafx.scene.shape._

/**
  * Created by Mitch on 8/2/2017.
  */
class Piece(evaluable: Evaluable) extends Draggable with Snapping {
  val delete_button = new Button("x")
  val clone_button = new Button("+")
  val edges = for (dir <-Direction.values) yield new PieceEdge(dir, evaluable.ports(dir))
  val center = new ImageView {
    image = Reader.IMAGES("default_center")
  }
  center.fitWidth   <== this.prefWidth  * 0.5
  center.fitHeight  <== this.prefHeight * 0.5
  center.translateX <== this.prefWidth  * 0.25
  center.translateY <== this.prefHeight * 0.25
  this.prefWidth  <== CircuitPane.tile_size
  this.prefHeight <== CircuitPane.tile_size

  this.children = Seq(
    center
  )

  def repaint(): Unit = {

  }

}

class PieceEdge(dir: Direction, port: Port) extends Pane {

  //val circuit_symbols: Seq[Path] = ???

  prefWidth  <== piece_edge_width
  prefHeight <== piece_edge_height
  translateX <== piece_edge_translateX(dir)
  translateY <== piece_edge_translateY(dir)


  children = Seq(
    //new HBox {
    //  children = circuit_symbols
    //}
  )

  def cross(size: Double): Path =
  new Path {
    elements = Seq(
    new MoveTo{x = 0;    y = 0},
    new LineTo{x = size; y = size},
    new MoveTo{x = size; y = 0},
    new LineTo{x = 0;    y = size}
    )
  }

  def circle(size: Double): Circle =
  new Circle {
    centerX = size*0.5
    centerY = size*0.5
    radius = size*0.5
  }
}

object Piece {
  val EDGE_WIDTH_PERCENTAGE = 0.4

  val piece_size:        NumberBinding = CircuitPane.tile_size * 1 //this should be a constant
  val piece_edge_width:  NumberBinding = piece_size * EDGE_WIDTH_PERCENTAGE
  val piece_edge_height: NumberBinding = piece_size * 0.5

  private val beta: NumberBinding = piece_size * (1 - EDGE_WIDTH_PERCENTAGE) * 0.5
  private val half: NumberBinding = piece_size * 0.5
  private val zero: NumberBinding = DoubleProperty(0) * 1 //this should be a constant
  val piece_edge_translateX = Array(beta, half, beta, zero)
  val piece_edge_translateY = Array(zero, beta, half, beta)
}
