package main.scala.graphical.controls

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane

/**
  * Created by Mitch on 8/2/2017.
  */
trait Draggable extends Pane {
  private val dragModeActiveProperty = new BooleanProperty(this, "dragModeActive", true)
  private val drag_context = new DragContext(0, 0, 0, 0)

  filterEvent(MouseEvent.Any) {
    (me: MouseEvent) =>
      if (dragModeActiveProperty()) {
        me.eventType match {
          case MouseEvent.MousePressed =>
            drag_context.mx = me.sceneX
            drag_context.my = me.sceneY
            drag_context.tx = this.translateX.toDouble
            drag_context.ty = this.translateY.toDouble
          case MouseEvent.MouseDragged =>
            this.translateX = drag_context.tx + me.sceneX - drag_context.mx
            this.translateY = drag_context.ty + me.sceneY - drag_context.my
          case _ => {}
        }
        me.consume()
      }
  }

  // Taken directly from the scalafx tutorial:
  // https://github.com/scalafx/ScalaFX-Tutorials/blob/master/event-filters/src/main/scala/event_filters/DraggablePanelsExample.scala
  case class DragContext(var mx: Double, var my: Double, var tx: Double, var ty: Double)
}
