package main.scala.graphical.screens

import javafx.event.Event
import javafx.scene.input.MouseEvent

import core.types.Coord
import graphical.screens.TitlePane
import io.{Level, Reader}
import main.scala.graphical.Main
import main.scala.graphical.controls.LevelDiagram

import scalafx.scene.control._
import scalafx.scene.layout.{HBox, Priority, VBox}

/**
  * Created by Mitch on 7/30/2017.
  */
object LevelSelectPane extends HBox {
  private val tree_view             = init_tree()
  private val level_name            = new Label("NAME OF LEVEL") {id = "level_name"}
  private val level_desc            = new Label("text goes here") {id = "level_desc"}
  private val level_diagram         = new LevelDiagram(300)
  private val level_completion_info = new Label("completion info") {id = "level_completion_info"}
  private val level_details         = new VBox {
    children = Seq(
      level_name,
      level_desc,
      level_diagram,
      level_completion_info,
      new Button(text = "Attempt")
    )
    id = "level_details"
    styleClass = Seq("bordered")
    hgrow = Priority.Always
    visible = false
  }

  children = Seq (
    new VBox {
      children = Seq(
        tree_view,
        new Button {
          text = "Menu"
          maxWidth = Double.MaxValue
          onMouseClicked = _ => Main.set_screen(TitlePane)
        }
      )
      styleClass = Seq("bordered")
    },
    level_details
  )
  id = "root"
  stylesheets = Seq(
    "@../../css/all-panes.css",
    "@../../css/level-select-pane.css"
  )

  init_tree()

  def set_level(level: Level): Unit = {
    level_diagram.set_level(level)
    level_details.setVisible(true)
    level_name.setText(level.name)
    level_desc.setText(level.instruction_text)
    level_completion_info.setText("Completed with size 8\nOptimal size " + level.optimal_size)
  }
  private def init_tree(): TreeView[LevelCell] =
    new TreeView[LevelCell] {
      root = new TreeItem[LevelCell] {
        children = for (i <- Reader.LEVEL_SETS.indices) yield
        new TreeItem[LevelCell] {
          children = for (j <- Reader.LEVEL_SETS(i).levels.indices)
            yield new TreeItem(new LevelCell(i, j))
        }
      }
      id = "tree_view"
      vgrow = Priority.Always
      showRoot = false
    }

  private class LevelCell(i: Int, j: Int) extends TreeCell[LevelCell] {

    //s"${i+1}. " + Reader.LEVEL_SET_NAMES(i)
    onMouseClicked = _ => println(s"($i, $j)")

    addEventHandler[MouseEvent](MouseEvent.MOUSE_CLICKED, _ => {})

    override def toString(): String = "%d.%d. %s".format(i+1, j+1, Reader.level(i, j).get.name)
  }
}