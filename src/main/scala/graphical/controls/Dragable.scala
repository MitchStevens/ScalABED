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
  private val drag_context = new DragContext

  filterEvent(MouseEvent.Any) {
    (me: MouseEvent) =>
      if (dragModeActiveProperty()) {
        me.eventType match {
          case MouseEvent.MousePressed =>
            drag_context.mouseAnchorX = me.x
            drag_context.mouseAnchorY = me.y
            drag_context.initialTranslateX = this.translateX.toDouble
            drag_context.initialTranslateY = this.translateY.toDouble
          case MouseEvent.MouseDragged =>
            this.translateX = drag_context.initialTranslateX + me.x - drag_context.mouseAnchorX
            this.translateY = drag_context.initialTranslateY + me.y - drag_context.mouseAnchorY
          case _ => {}
        }
        me.consume()
      }
  }

  // Taken directly from the scalafx tutorial:
  // https://github.com/scalafx/ScalaFX-Tutorials/blob/master/event-filters/src/main/scala/event_filters/DraggablePanelsExample.scala
  private final class DragContext {
    var mouseAnchorX: Double = 0
    var mouseAnchorY: Double = 0
    var initialTranslateX: Double = 0
    var initialTranslateY: Double = 0
  }
}
