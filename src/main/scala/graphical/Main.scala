package main.scala.graphical

import graphical.screens.TitlePane

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.binding.NumberBinding
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
  val screen_pane = new StackPane()

  stage = new PrimaryStage {
    title = "Digital Sage, Version: "+ VERSION
    width  = DEFAULT_WIDTH
    height = DEFAULT_HEIGHT
    scene = new Scene(screen_pane)
  }

  val board_width:  NumberBinding = stage.width  * 1
  val board_height: NumberBinding = stage.height * 1

  set_screen(TitlePane)

  def set_screen(pane: Pane): Unit = {
    screen_pane.children = pane
  }
}
