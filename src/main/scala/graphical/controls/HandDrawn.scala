package main.scala.graphical.controls

import scala.math.hypot
import scala.util.Random
import scalafx.scene.shape.{CubicCurveTo, MoveTo, Path, PathElement}

/**
  * Created by Mitch on 7/30/2017.
  */
object HandDrawn {
  private val CONST = 0.05
  private val r = new Random(System.currentTimeMillis)

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Seq[PathElement] = {
    val distance = hypot(x2 - x1, y2 - y1)
    val eps: Stream[Double] = Stream.continually(r.nextDouble).map(d => (d - .5)*distance*CONST)

    Seq(
      new MoveTo{x = x1; y = y1},
      new CubicCurveTo {
        controlX1 = x1 + eps(0)
        controlY1 = y1 + eps(1)
        controlX2 = x2 + eps(2)
        controlY2 = y2 + eps(3)
        x = x2; y = y2
      }
    )
  }

  def rectangle(x1: Double, y1: Double, x2: Double, y2: Double): Seq[PathElement] =
    Seq(
      line(x1, y1, x2, y1),
      line(x2, y1, x2, y2),
      line(x2, y2, x1, y2),
      line(x1, y2, x1, y1)
    ).flatten
}




















