package test

import core.circuit.MesaCircuit
import core.types.Direction
import core.types.ID.ID

import scalafx.application.JFXApp
import scalafx.event.Event
import scalafx.geometry.Side
import scalafx.scene.control.Label
import scalafx.scene.layout.{BorderPane, Pane}
import scalafx.scene.shape.Circle

/**
  * Created by Mitch on 9/16/2017.
  */
class CircuitStateVisualiser(var f: MesaCircuit, var changes: Seq[MesaCircuit => MesaCircuit]) extends JFXApp {



  def next_change(): Unit = {

  }

  def repaint(): Unit = ???

  def mouseover_text(side: Side): String = {
    val current_state = f.state
    val port_type = f.graphs
    s"Port State: $current_state\n" +
      s"Port Type: $port_type"

  }

}

class A(id: ID, mouseover: Array[String]) extends BorderPane {
  private val size = 50.0
  center = new Label(text = id)
  top    = circle(0)
  right  = circle(1)
  bottom = circle(2)
  left   = circle(3)

  def circle(dir: Direction): Circle =
    new Circle {
      radius = size*0.2
      //onMouseEntered((e: Event) => println(mouseover(dir)))
    }
}