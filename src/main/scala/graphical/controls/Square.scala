package main.scala.graphical.controls

import javafx.css.PseudoClass

import core.Positional
import core.types.Coord.Location
import core.types.{Coord, Direction}
import main.scala.graphical.screens.CircuitPane

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Polygon

/**
  * Created by Mitch on 7/28/2017.
  */
class Square(private var pos: Coord) extends Pane with Positional {
  import Square._
  val m = 0.05
  val chevron = new Polygon {
    //points = Seq(
    //  0.5, 0+m, 1-m, 0.5, 1-m, 1-m,
    //  0.5, 0.5, 0+m, 1-m, 0+m, 0.5
    //)
    styleClass = Seq("chevron")
  }
  //chevron.points = Seq()
  prefWidth  <== CircuitPane.tile_size
  prefHeight <== CircuitPane.tile_size
  chevron.scaleX <== this.width
  chevron.scaleY <== this.height

  set_psuedoclasses(CircuitPane.num_tiles.toInt)
  CircuitPane.num_tiles onChange ( (_, _, n) => {
    set_psuedoclasses(n.intValue())
  })
  children = Seq(chevron, new Label(pos.toString))
  styleClass = Seq("square")
  this.set_translate()

  def chevron_direction(dir: Direction): Unit =
    chevron.setRotate(90 * dir.n)

  def show_chevron(bool: Boolean): Unit =
    chevron.setVisible(bool)

  def set_psuedoclasses(n: Int): Unit = {
    val location = pos.on_side(n.intValue())
    this.pseudoClassStateChanged(EDGE_PSEUDO_CLASS,   location == Location.EDGE)
    this.pseudoClassStateChanged(CORNER_PSEUDO_CLASS, location == Location.CORNER)
  }

  override def position: Coord = pos
  override def move(pos: Coord): Unit = {
    this.pos = pos
    this.set_translate()
  }

  def set_translate(): Unit = {
    translateX <== CircuitPane.tile_originX + CircuitPane.tile_size * pos.x
    translateY <== CircuitPane.tile_originY + CircuitPane.tile_size * pos.y
  }

}

object Square {
  val EDGE_PSEUDO_CLASS:   PseudoClass = PseudoClass.getPseudoClass("edge")
  val CORNER_PSEUDO_CLASS: PseudoClass = PseudoClass.getPseudoClass("corner")
}