package main.scala.graphical

import io.{Level, Reader}
import main.scala.graphical.controls.{HandDrawn, LevelDiagram}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, TilePane}
import scalafx.scene.shape.Path
import scalafx.stage.StageStyle

/**
  * Created by Mitch on 7/31/2017.
  */
object Test extends JFXApp {
  stage = new PrimaryStage {
    scene = new Scene {
      root = tile_pane
    }
  }

  def tile_pane: TilePane =
    new TilePane {
      prefWidth  = 1024.0
      prefHeight = 768.0
      children = Seq(
        hand_drawn_line,
        hand_drawn_square,
        level_diagram
      )
    }

  def hand_drawn_line: Pane =
    new Pane {
      prefWidth  = 200.0
      prefHeight = 200.0
      children = new Path {
        elements = HandDrawn.line(50, 50, 150, 150)
      }
    }

  def hand_drawn_square: Pane =
    new Pane {
      prefWidth  = 200.0
      prefHeight = 200.0
      children = new Path {
        elements = HandDrawn.rectangle(50, 50, 150, 150)
      }
    }

  def level_diagram: Pane = {
    val diagram: LevelDiagram = new LevelDiagram(150.0) {
      layoutX = 25.0
      layoutY = 25.0
    }
    diagram.set_level(Reader.level(1, 1).get)
    new Pane {
      prefWidth  = 200.0
      prefHeight = 200.0
      children = diagram
    }
  }
}
