package core

import javafx.beans.Observable

import core.Expression.Signal

/**
  * Created by Mitch on 3/18/2017.
  */

trait Evaluable extends Circuit {

  val num_inputs:  Array[Int]
  val num_outputs: Array[Int]
  val last_inputs:  Array[Signal]
  val last_outputs: Array[Signal]

  def set_inputs(ins: Array[Signal]): Unit
  def calc_outputs(): Unit

}