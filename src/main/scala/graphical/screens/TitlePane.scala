package graphical.screens

import java.io.File

import core.circuit.Game
import io.Reader
import main.scala.graphical.Main
import main.scala.graphical.screens.{CircuitPane, GamePane, LevelSelectPane}

import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{StackPane, VBox}

/**
  * Created by Mitch on 7/28/2017.
  */
object TitlePane extends StackPane {
  children = Seq(
    new VBox {
      children = Seq(
        new Label("Digital Sage") {styleClass = List("title")},
        new Label("Play") {
          styleClass =Seq("a")
          onMouseClicked = _ => Main.set_screen(LevelSelectPane)
        },
        new Label("Sandbox") {
          styleClass =Seq("a")
          CircuitPane.set_game(new Game(5))
          onMouseClicked = _ => Main.set_screen(GamePane)
        },
        new Label("Exit") {
          styleClass =Seq("a")
          onMouseClicked = _ => System.exit(0)
        }
      )
    }
  )
  stylesheets = Seq(
    "css/all-panes.css",
    "css/title-pane.css"
  )
  id = "root"
  prefWidth  <== Main.board_width
  prefHeight <== Main.board_height
}
