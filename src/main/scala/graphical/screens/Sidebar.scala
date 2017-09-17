package main.scala.graphical.screens

import core.types.Coord
import graphical.screens.TitlePane
import io.Reader
import main.scala.graphical.Main
import main.scala.graphical.controls.{Incrementer, Piece}

import scalafx.scene.control._
import scalafx.scene.layout.VBox

/**
  * Created by Mitch on 7/31/2017.
  */
object Sidebar extends TabPane {
  val WIDTH: Double = 300.0
  val incrementer = new Incrementer(3, 10, 5)

  prefHeight <== GamePane.inner_pane_height
  prefWidth  = WIDTH
  translateX <== GamePane.width - WIDTH - GamePane.SPACING
  translateY = GamePane.SPACING
  tabs = Seq(
    new Tab {
      text = "Info"
      content = new VBox {
        children = Seq(
          new Label("TITLE") {styleClass = Seq("level_name")},
          new TextArea {
            id = "description"
            wrapText = true
          },
          new TitledPane {
            id = "hint"
            text = "Hint"
          },
          new Button {
            text = "Add a piece"
            maxWidth = Double.MaxValue
            onMouseClicked = _ => {}//CircuitPane.add(Reader.MAPPINGS("AND"), Coord(0, 0))}
          },
          new Button {
            text = "Menu"
            maxWidth = Double.MaxValue
            onMouseClicked = _ => Main.set_screen(TitlePane)
          }
        )
      }
    },
    new Tab {
      text = "Game"
      content = new VBox {
        children = Seq(
          new Label("Game Size:"),
          incrementer
        )
      }
    }
  )
  styleClass = Seq("bordered")
}
