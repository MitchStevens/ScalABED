package main.scala.graphical

import graphical.screens.TitlePane
import main.scala.graphical.screens.{GamePane, LevelSelectPane}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.DoubleProperty
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, StackPane}

/**
  * Created by Mitch on 7/29/2017.
  */
object Main extends JFXApp {
  val VERSION = "0.3"
  val MIN_WIDTH:      Double = 800.0
  val MIN_HEIGHT:     Double = 600.0
  val DEFAULT_HEIGHT: Double = 768.0
  val DEFAULT_WIDTH:  Double = 1024.0
  val board_width  = DoubleProperty(DEFAULT_WIDTH)
  val board_height = DoubleProperty(DEFAULT_HEIGHT)

  val screen_pane = new StackPane() {children = Seq(TitlePane)}
  stage = new PrimaryStage {
    width  = DEFAULT_WIDTH
    height = DEFAULT_HEIGHT
    scene = new Scene {
      root = screen_pane
    }
  }
  board_width  <== stage.width
  board_height <== stage.height

  def set_screen(pane: Pane): Unit = {
    screen_pane.children = pane
  }
}
