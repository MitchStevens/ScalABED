package main.scala.graphical.controls

import core.Positional
import core.types.Coord
import main.scala.graphical.screens.CircuitPane

import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane

/**
  * Created by Mitch on 8/6/2017.
  */
trait Snapping extends Draggable with Positional { piece: Piece =>

  filterEvent(MouseEvent.Any) {
    (me: MouseEvent) =>
      me.eventType match {
        case MouseEvent.MouseReleased => {
          val s = get_closest(me)
          if (s._2.position == this.position) {
            piece.set_translateX()
            piece.set_translateY()
          } else if (s._1 < CircuitPane.tile_size.toDouble)
            CircuitPane.move(this.position, s._2.position)
          else
            CircuitPane.remove(this.position)
          me.consume()
        }
        case _ => {}
      }
  }

  //Could this be done with A*? Probably not worth it.
  private def get_closest(me: MouseEvent): (Double, Square) = {
    val x = this.translateX()
    val y = this.translateY()
    def cmp(s: Square): Double = math.hypot(x - s.translateX(), y - s.translateY())
    val min = CircuitPane.squares.values minBy cmp
    (cmp(min), min)
  }
}
