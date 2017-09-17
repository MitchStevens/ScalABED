package main.scala.graphical.screens

import main.scala.graphical.Main

import scalafx.beans.binding.NumberBinding
import scalafx.scene.layout.{BorderPane, Pane}

/**
  * Created by Mitch on 7/31/2017.
  */
object GamePane extends Pane {
  val SPACING: Double = 50.0
  val PADDING: Double = 20.0
  val inner_pane_width:  NumberBinding = this.width - 2 * SPACING
  val inner_pane_height: NumberBinding = this.height - 2 * SPACING

  id = "root"
  children = Seq(CircuitPane,Sidebar)
  stylesheets = Seq(
    "@../../css/all-panes.css",
    "@../../css/game_pane.css",
    "@../../css/circuit-pane.css",
    "@../../css/square.css"
  )
  prefWidth  <== Main.board_width
  prefHeight <== Main.board_height

}
