package main.scala.graphical.screens

import graphical.controls.AutoCompleteField
import io.Reader
import main.scala.graphical.Main

import scalafx.beans.binding.NumberBinding
import scalafx.scene.layout.Pane

/**
  * Created by Mitch o n 7/31/2017.
  */
object GamePane extends Pane {
  val SPACING: Double = 50.0
  val PADDING: Double = 20.0
  val inner_pane_width:  NumberBinding = this.width - 2 * SPACING
  val inner_pane_height: NumberBinding = this.height - 2 * SPACING
  val debug_cheats = true
  def cheats(str: String): Unit = {
    val rot = """_rot (\d) (\d)""".r
    val add = """_add (\w*) \* (\d)""".r
    str match {
      case rot(x, y)  => CircuitPane.rotate((x.toInt, y.toInt), 1)
      case add(name, num) => (1 to num.toInt) foreach (_ => CircuitPane.add_somewhere(Reader.evaluable(name)))
      case "_state"   => println(CircuitPane.current_game.state)
      case "_coords"  => println(CircuitPane.current_game.coord_map)
      case "_init"    => GamePaneTests.init()
      case "_test1"   => GamePaneTests.test1()
      case "_test2"   => GamePaneTests.test2()
      case "_test3"   => GamePaneTests.test3()
      case _ => {}
    }
  }

  val search_bar = new AutoCompleteField() {
    visible = false
  }

  this.setOnKeyPressed(_.getCode.getName match {
    case "Enter" =>
      val text = search_bar.getText
      /*
      if (Reader.MAPPINGS.contains(text)) {
        CircuitPane.add_somewhere(Reader.evaluable(text))
        search_bar.clear()
        search_bar.visible = false
      } else*/ if (text.isEmpty)
        search_bar.visible = false
      else cheats(text)
    case "Shift" =>
      if (!search_bar.isVisible) {
        search_bar.visible = true
        search_bar.requestFocus()
      }
    case _ => {}
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

protected object GamePaneTests {
  def init(): Unit =
    for (name <- Seq("INPUT", "NOT", "NOT", "BUS", "TRUE", "FALSE", "XOR", "AND"))
      CircuitPane.add_somewhere(Reader.MAPPINGS(name))

  def test1(): Unit = {
    CircuitPane.add_somewhere(Reader.evaluable("XOR"))
    CircuitPane.add_somewhere(Reader.evaluable("TRUE"))
    CircuitPane.move((0, 0), (1, 1))
    CircuitPane.move((0, 1), (1, 0))
  }

  def test2(): Unit = {
    CircuitPane.add(Reader.evaluable("TRUE"), (2, 0))
    CircuitPane.rotate((2, 0), 1)
    CircuitPane.add(Reader.evaluable("TRUE"), (0, 2))
    CircuitPane.add(Reader.evaluable("SUPER"), (2, 1))
    CircuitPane.rotate((2, 1), 1)
    CircuitPane.add(Reader.evaluable("SUPER"), (1, 2))
    CircuitPane.add(Reader.evaluable("SUPER"), (3, 2))
    CircuitPane.add(Reader.evaluable("BUS"), (3, 1))
    CircuitPane.add(Reader.evaluable("BUS"), (2, 3))
    CircuitPane.add(Reader.evaluable("BUS"), (4, 3))
    CircuitPane.add(Reader.evaluable("RIGHT"), (4, 1))
    CircuitPane.add(Reader.evaluable("RIGHT"), (5, 2))
    CircuitPane.add(Reader.evaluable("LEFT"), (1, 3))
    CircuitPane.rotate((1, 3), 1)
    CircuitPane.add(Reader.evaluable("NAND"), (2, 2))
    CircuitPane.add(Reader.evaluable("NAND"), (3, 3))
    CircuitPane.add(Reader.evaluable("NAND"), (4, 2))
    CircuitPane.add(Reader.evaluable("NAND"), (5, 3))
  }

  def test3(): Unit = {
    CircuitPane.add(Reader.evaluable("TRUE"), (0, 0))
    CircuitPane.rotate((0, 0), 1)
    CircuitPane.add(Reader.evaluable("NAND"),  (0, 1))
    CircuitPane.rotate((0, 1), 1)
    CircuitPane.add(Reader.evaluable("LEFT"), (0, 2))
    CircuitPane.rotate((0, 2), 1)
    CircuitPane.add(Reader.evaluable("TRUE"), (1, 3))
    CircuitPane.rotate((1, 3), -1)
    CircuitPane.add(Reader.evaluable("NAND"),  (1, 2))
    CircuitPane.rotate((1, 2), -1)
    CircuitPane.add(Reader.evaluable("LEFT"), (1, 1))
    CircuitPane.rotate((1, 1), -1)
  }
}