package main.scala.graphical.controls

import core.circuit.{Evaluable, Port}
import core.types.{Coord, Direction, Side}
import main.scala.graphical.screens.CircuitPane
import Piece._
import core.{Paintable, Positional}
import core.circuit.Port.PortType
import core.types.Signal._
import core.CatzInstances._
import io.Reader
import main.scala.graphical.Main

import scalafx.beans.binding._
import scalafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Pane}

/**
  * Created by Mitch on 8/2/2017.
  */
class Piece(evaluable: Evaluable, var position: Coord)  extends Snapping
                                                        with    Positional
                                                        with    Paintable[(Direction, Array[Signal])] {
  val edges: Seq[PieceEdge] = Direction.values map (new PieceEdge(_))
  val center = new ImageView {
    image = Reader.IMAGES.get("center_" + evaluable.name) getOrElse {
      evaluable.name match {
        case "LEFT"  => Reader.IMAGES("center_BUS")
        case "RIGHT" => Reader.IMAGES("center_BUS")
        case "SUPER" => Reader.IMAGES("center_BUS")
        case _       => Reader.IMAGES("center_DEFAULT")
      }
    }
  }
  center.fitWidth   <== this.prefWidth  * 0.5
  center.fitHeight  <== this.prefHeight * 0.5
  center.translateX <== this.prefWidth  * 0.25
  center.translateY <== this.prefHeight * 0.25

  this.prefWidth  <== CircuitPane.tile_size
  this.prefHeight <== CircuitPane.tile_size
  set_translateX()
  set_translateY()
  Main.board_width  onChange set_translateX()
  Main.board_height onChange set_translateY()

  def set_translateX(): Unit = {translateX = CircuitPane.squares(this.position).translateX.value}
  def set_translateY(): Unit = {translateY = CircuitPane.squares(this.position).translateY.value}

  this.toFront()
  this.children = Seq(center) ++ edges

  this.setOnScroll(e => {
    val rot = if (e.getDeltaY < 0) 1 else -1
    CircuitPane.rotate(this.position, rot)
  })

  override def repaint(t: (Direction, Array[Signal])): Unit = {
    this.rotate = t._1 * 90.0
    for (dir <- Direction.values)
      edges(dir) repaint t._2(dir)
  }

  class PieceEdge(dir: Direction) extends Pane with Paintable[Signal] {
    prefWidth  <== piece_edge_width
    prefHeight <== piece_edge_height
    translateX <== piece_edge_translateX(dir)
    translateY <== piece_edge_translateY(dir)
    val port_image = new ImageView {
      image = evaluable.ports(dir).port_type match {
        case PortType.OUT    => Reader.IMAGES("port_out")
        case PortType.IN     => Reader.IMAGES("port_in")
        case PortType.UNUSED => Reader.IMAGES("port_unused")
      }
    }
    port_image.fitWidth  <== prefWidth
    port_image.fitHeight <== prefHeight
    val signal_image = new ImageView()
    signal_image.fitWidth  <== prefWidth  * 0.4
    signal_image.fitHeight <== prefHeight * 0.4
    signal_image.translateX <== (width - signal_image.fitWidth) * 0.5
    rotate = dir * 90.0
    children = Seq(port_image, signal_image)

    override def repaint(t: Signal): Unit = {
      val a =
        if (t.isEmpty) "unused"
        else if (t.or) "on"
        else           "off"
      signal_image.image = Reader.IMAGES(a)
    }
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
