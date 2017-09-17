package main.scala.graphical

import cats.Monad

import scala.collection.GenTraversableOnce
import scala.collection.generic.CanBuildFrom
import scalafx.Includes._
import scalafx.beans.binding.NumberBinding
import scalafx.beans.binding.NumberBinding._
import scalafx.beans.value.ObservableValue

/**
  * Created by Mitch on 8/7/2017.
  */
trait MonadicBinding[Number] extends ObservableValue[Number, Number] with Iterable[Number] {
  //override def flatMap[B, That](f: (Number) => GenTraversableOnce[B]): That = super.flatMap(f)
}
