package main.scala.graphical.screens

import main.scala.graphical.Main

import scalafx.scene.layout.BorderPane

/**
  * Created by Mitch on 7/31/2017.
  */
object GamePane extends BorderPane {
  id = "root"
  right = Sidebar
  center = CircuitPane
  stylesheets = Seq(
    "@../../css/all-panes.css",
    "@../../css/game_pane.css",
    "@../../css/circuit-pane.css",
    "@../../css/square.css"
  )
  prefWidth  <== Main.board_width
  prefHeight <== Main.board_height
}
