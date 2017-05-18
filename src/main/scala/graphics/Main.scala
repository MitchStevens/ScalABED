package graphics

import graphics.CircuitPane
import javafx._
import application.Application
import javafx.scene.layout.{Pane, Region}
import scene.{Group, Scene}
import stage.Stage

/**
  * Created by Mitch on 5/16/2017.
  */
class Main extends Application {
  var root:  Pane  = new Pane
  var scene: Scene = new Scene(root, Main.DEFAULT_WIDTH, Main.DEFAULT_HEIGHT)

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    stage.setTitle("ABED: Version " + Main.VERSION)
    stage.setScene(scene)
    stage.setMinHeight(Main.MIN_HEIGHT)
    stage.setMinWidth(Main.MIN_WIDTH)
    stage.show()

    square_test()
  }

  private def square_test(): Unit = {
    val pane = CircuitPane
    pane.set_size(3)
    root.getChildren.add(pane)
  }
}

object Main {
  val VERSION = "2.0A"

  val SIDE_BAR_WIDTH: Double = 300.0
  val MIN_WIDTH:      Double = 800.0
  val MIN_HEIGHT:     Double = 600.0
  val DEFAULT_HEIGHT: Double = 768.0
  val DEFAULT_WIDTH:  Double = 1024.0

  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }

}