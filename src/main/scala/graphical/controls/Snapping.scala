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
trait Snapping extends Draggable with Positional {

  filterEvent(MouseEvent.Any) {
    (me: MouseEvent) =>
      me.eventType match {
        case MouseEvent.MouseReleased => {
          val s = get_closest(me)
          //CircuitPane.move(this.position, s.position)
        }
        case _ => {}
      }
  }

  //Could this be done with A*? Probably not worth it.
  private def get_closest(me: MouseEvent): Square = {
    val x = this.translateX()
    val y = this.translateY()
    def cmp(s: Square): Double = math.hypot(x - s.translateX(), y - s.translateY())
    CircuitPane.squares.values minBy cmp
  }
}
