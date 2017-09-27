package main.scala.graphical.screens

import graphical.controls.AutoCompleteField
import io.Reader
import main.scala.graphical.Main

import scalafx.animation.FillTransition
import scalafx.beans.binding.NumberBinding
import scalafx.scene.control.TextField
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{BorderPane, Pane}
import scalafx.util.Duration

/**
  * Created by Mitch on 7/31/2017.
  */
object GamePane extends Pane {
  val SPACING: Double = 50.0
  val PADDING: Double = 20.0
  val inner_pane_width:  NumberBinding = this.width - 2 * SPACING
  val inner_pane_height: NumberBinding = this.height - 2 * SPACING
  val debug_cheats = true
  val cheats: Map[String, () => Unit] = Map(
    "_state" -> (() => println(CircuitPane.current_game.state))
  )

  val search_bar = new AutoCompleteField() {
    visible = false
  }

  this.setOnKeyPressed(_.getCode.getName match {
    case "Enter" =>
      val text = search_bar.getText
      if (Reader.MAPPINGS.contains(text)) {
        CircuitPane.add_somewhere(Reader.evaluable(text))
        search_bar.clear()
        search_bar.visible = false
      } else if (text.isEmpty)
        search_bar.visible = false
      else if (debug_cheats && cheats.contains(text)) {
        cheats(text)()
        search_bar.clear()
        search_bar.visible = false
      } else {}
    case "Shift" =>
      if (!search_bar.isVisible) {
        search_bar.visible = true
        search_bar.requestFocus()
      }
    case e => {println(s"$e does nothing")}
  })

  id = "root"
  children = Seq(CircuitPane, Sidebar, search_bar)
  stylesheets = Seq(
    "@../../css/all-panes.css",
    "@../../css/game_pane.css",
    "@../../css/circuit-pane.css",
    "@../../css/square.css"
  )
  prefWidth  <== Main.board_width
  prefHeight <== Main.board_height

}
