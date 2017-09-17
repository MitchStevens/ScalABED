package main.scala.graphical.controls

import core.circuit.{Evaluable, Port}
import core.types.{Coord, Direction}
import main.scala.graphical.screens.CircuitPane
import Piece._
import core.Positional
import core.circuit.Port.PortType
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
class Piece(evaluable: Evaluable, var pos: Coord) extends Snapping with Positional {
  val delete_button = new Button("x")
  val clone_button = new Button("+")
  val edges: Seq[PieceEdge] = for (d <- Direction.values) yield new PieceEdge(d)
  val center = new ImageView {
    image = Reader.IMAGES("default_center")
  }
  center.fitWidth   <== this.prefWidth  * 0.5
  center.fitHeight  <== this.prefHeight * 0.5
  center.translateX <== this.prefWidth  * 0.25
  center.translateY <== this.prefHeight * 0.25
  this.prefWidth  <== CircuitPane.tile_size
  this.prefHeight <== CircuitPane.tile_size
  this.translateX = CircuitPane.squares(pos).translateX.value
  this.translateY = CircuitPane.squares(pos).translateY.value

  this.children = Seq(center) ++ edges

  def repaint(): Unit = {

  }

  override def position: Coord = pos
  override def move(pos: Coord): Unit = {this.pos = pos}

  class PieceEdge(dir: Direction) extends Pane {
    prefWidth  <== piece_edge_width
    prefHeight <== piece_edge_height
    translateX <== piece_edge_translateX(dir)
    translateY <== piece_edge_translateY(dir)
    val port_image = new ImageView {
      image = evaluable.ports(dir).port_type match {
        case PortType.OUT    => Reader.IMAGES("out")
        case PortType.IN     => Reader.IMAGES("in")
        case PortType.UNUSED => Reader.IMAGES("unused")
      }
      rotate = dir * 90.0
    }
    port_image.fitWidth  <== prefWidth
    port_image.fitHeight <== prefHeight
   children = Seq(port_image)
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
