package main.scala.graphical.controls

import scalafx.beans.property.IntegerProperty
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.text.Text

/**
  * Created by Mitch on 7/30/2017.
  */
class Incrementer(val min_value: Int, val max_value: Int, def_value: Int) extends BorderPane {
  assert(min_value <= def_value && def_value <= max_value)

  val num_label = new Text(def_value.toString) {id = "num_label"}
  val value = IntegerProperty(def_value)
  value onChange {(_, _, n) => num_label.text = n.toString}

  def decrement(): Unit = {
    val m = this.value.get
    if (m > min_value)
      value.setValue(m - 1)
  }

  def increment(): Unit = {
    val m = this.value.get
    if (m < max_value)
      value.setValue(m + 1)
  }

  def set_value(v: Int): Unit =
    if (min_value < v && v < max_value)
      value.set(v)

  maxWidth  = Double.NegativeInfinity
  maxHeight = Double.NegativeInfinity
  prefWidth = 160.0
  stylesheets = Seq("@../../css/increment.css")

  left = new Button("-") {
    id = "dec_button"
    onMouseClicked = _ => decrement()
  }
  center = num_label
  right = new Button("+") {
    id = "inc_button"
    onMouseClicked = _ => increment()
  }
  styleClass = Seq("incrementer")
}
