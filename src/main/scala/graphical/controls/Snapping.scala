package main.scala.graphical.controls

import core.types.Coord
import main.scala.graphical.screens.CircuitPane

import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane

/**
  * Created by Mitch on 8/6/2017.
  */
trait Snapping extends Draggable {

  filterEvent(MouseEvent.Any) {
    (me: MouseEvent) =>
      me.eventType match {
        case MouseEvent.MouseReleased => {
          val sc = get_closest(me)
          this.translateX = sc.position._1
          this.translateY = sc.position._2
        }
        case _ => {}
      }
  }

  //Could this be done with A*? Probably not worth it.
  private def get_closest(me: MouseEvent): SnapContext = {
    val b1 = this.localToScene(this.getBoundsInLocal)
    def weird_min(sc: SnapContext, t: (Coord,Square)): SnapContext = {
      val b2 = t._2.
      val pos = (b2.getMinX, b2.getMinY)
      val d = math.hypot(b1.getMinX - pos._1, b1.getMinY - pos._2)


      println("")
      if (d < sc.distance)
        new SnapContext(t._1, t._2, pos, d)
      else sc
    }
    CircuitPane.squares.foldLeft(SnapContext(null, null, null, Double.PositiveInfinity))(weird_min)
  }

  case class SnapContext(coord: Coord, square: Square, position: (Double, Double), distance: Double)

}
