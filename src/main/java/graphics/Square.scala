package graphics

import Square._

import javafx.css.PseudoClass
import javafx.scene.layout.Region
import javafx.scene.shape.{Polygon, Rectangle}

import core.types.{Coord, Direction}

/**
  * Created by Mitch on 5/16/2017.
  */

/*
*
* */
class Square(pos: Coord) extends Region with Resizable {
  private val square: Rectangle = new Rectangle
  private val chevron: Polygon = new Polygon(
    0.0,  1.0,
    0.0,  0.5,
    0.5,  0.0,
    1.0,  0.5,
    1.0,  1.0,
    0.5,  0.5
  )

  this.setId("square")
  square.getStyleClass.add("square")
  square.setX(100)
  square.setY(100)
  chevron.getStyleClass.add("chevron")
  chevron.prefHeight(100)
  chevron.prefWidth(100)
  this.getChildren.addAll(square, chevron)

  /*
  *
  * */
  def set_fill(size: Int): Unit = {
    val num_edges: Int = pos.on_side(size)
    this.pseudoClassStateChanged(EDGE_PSEUDO_CLASS,   num_edges == 1)
    this.pseudoClassStateChanged(CORNER_PSEUDO_CLASS, num_edges == 2)
  }

  def chevron_direction(direction: Direction): Unit =
    chevron.setRotate(90 * direction.n)

  def show_chevron(bool: Boolean): Unit =
    chevron.setVisible(bool)

}

object Square {
  val EDGE_PSEUDO_CLASS   = PseudoClass.getPseudoClass("edge")
  val CORNER_PSEUDO_CLASS = PseudoClass.getPseudoClass("corner");
}
