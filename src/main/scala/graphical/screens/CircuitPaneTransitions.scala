package graphical.screens

import io.Reader
import main.scala.graphical.controls.Square
import main.scala.graphical.screens.CircuitPane

import scalafx.animation._
import scalafx.scene.Node
import scalafx.scene.image.ImageView
import scalafx.util.Duration

object CircuitPaneTransitions {
  def add(node: Node): Transition = {
    val scale_t = new ScaleTransition(Duration(200), node) {
      fromX = 0.0
      fromY = 0.0
      toX = 1.0
      toY = 1.0
    }
    val rotate_t = new RotateTransition(Duration(200), node) {
      fromAngle = -20
      toAngle = 0
    }
    new ParallelTransition(children = Seq(scale_t, rotate_t))
  }

  def remove(node: Node): Transition = {
    val scale_t = new ScaleTransition(Duration(200), node) {
      fromX = 1.0
      fromY = 1.0
      toX = 0.0
      toY = 0.0
    }
    val rotate_t = new RotateTransition(Duration(200), node) {
      fromAngle = 0
      toAngle = 20
    }
    new ParallelTransition(children = Seq(scale_t, rotate_t))
  }

  def move(node: Node, square: Square): Transition =
    new TranslateTransition(Duration(75), node) {
      toX = square.translateX.value
      toY = square.translateY.value
    }

  def toggle(x_pos: Double, y_pos: Double): Transition = {
    val focus_explosion = new ImageView() {
      image = Reader.IMAGES("toggle_focus")
      fitHeight  = CircuitPane.tile_size.toDouble
      fitWidth   = CircuitPane.tile_size.toDouble
      translateX = x_pos
      translateY = y_pos
    }
    CircuitPane.children.add(focus_explosion)
    val grow_t = new ScaleTransition(Duration(200), focus_explosion) {
      fromX = 0.0
      fromY = 0.0
      toX   = 1.0
      toY   = 1.0
    }
    val fade_t = new FadeTransition(Duration(100), focus_explosion) {
      delay = Duration(100)
      fromValue = 1.0
      toValue   = 0.0
    }
    new ParallelTransition() {
      children = Seq(grow_t, fade_t)
      onFinished = _ => CircuitPane.children.remove(focus_explosion)
    }
  }
}
