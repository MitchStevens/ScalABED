package main.scala.graphical.controls

import scala.math._
import core.circuit.Port
import core.circuit.Port.PortType
import core.types.Direction
import io.Level

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Path, PathElement, Rectangle}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.paint.Color

/**
  * Created by Mitch on 7/30/2017.
  */
class LevelDiagram(size: Double) extends Pane {
  private val OFFSET = 0.15
  private val ARROW_ANGLE = Pi * 0.65
  private val ARROW_LENGTH = 0.13
  private val root = new Pane()

  children = root
  prefWidth  = size
  prefHeight = size

  def set_level(level: Level): Unit = {
    def make_label(d: Direction): Seq[Node] = {
      val port = Port.create(level.inputs(d), level.outputs(d))
      if (port.port_type != PortType.UNUSED) {
        val arrow_rot = Direction(if (port.is_output) d else d+2)
        Seq (arrow(Direction(d), arrow_rot),
             number(Direction(d), port.capacity)
        )
      } else Seq.empty[Node]
    }

    val offset = OFFSET * size
    this.root.children = Seq(
        new Rectangle {x = 0; y = 0; width = size; height = size; fill = Color.AntiqueWhite},
        new Path {elements = HandDrawn.rectangle(offset, offset, size - offset, size - offset)}
      ) ++ Direction.values.flatMap(make_label)
  }


  private def arrow(side: Direction, arrow_rotation: Direction): Path = {
    val ARROW_POSITIONS = Seq(
      Seq(0.5,          OFFSET),
      Seq(1.0 - OFFSET, 0.5),
      Seq(0.5,          1.0 - OFFSET),
      Seq(OFFSET,       0.5)
    )
    val a = OFFSET * size
    val p = OFFSET / (2 * tan(ARROW_ANGLE / 2)) * size
    val c = cos(ARROW_ANGLE / 2) * ARROW_LENGTH * size
    val s = sin(ARROW_ANGLE / 2) * ARROW_LENGTH * size

    new Path {
      elements = Seq(
        HandDrawn.line(a*.5,  -a+p, a*.5,   a),
        HandDrawn.line(-a*.5, -a+p, -a*.5,  a),
        HandDrawn.line(0,     -a,   s,   -a+c),
        HandDrawn.line(0,     -a,   -s,  -a+c)
      ).flatten
      rotate = arrow_rotation * 90.0
      layoutX = ARROW_POSITIONS(side)(0) * size
      layoutY = ARROW_POSITIONS(side)(1) * size
    }
  }

  private def number(side: Direction, capacity: Int): Label = {
    val LABEL_POSITIONS = Seq (
      Seq(0.5 - 1.5 * OFFSET, 0.0),
      Seq(1.0 - OFFSET, 0.5 - 1.5 * OFFSET),
      Seq(0.5 - 1.5 * OFFSET, 1.0 - OFFSET),
      Seq(0.0, 0.5 - 1.5 * OFFSET)
    )
    val FONT_OFFSET = 0.02 //Change this after changing font size
    val a = OFFSET * size

    new Label {
      text = s"$capacity"
      styleClass = Seq("capacity_marker")
      prefWidth  = a
      prefHeight = a
      alignment = if (side.n == 1) Pos.CenterLeft else Pos.CenterRight
      layoutX = LABEL_POSITIONS(side)(0) * size
      layoutY = (LABEL_POSITIONS(side)(1) - FONT_OFFSET) * size
    }
  }
}















