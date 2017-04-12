package core

import core.MetaCircuit.{Edge, ID}
import core.data.{Direction, Signal}
import core.data.Signal.Signal

import scala.collection.mutable.HashMap
import scala.concurrent.Future


/**
  * Created by Mitch on 3/20/2017.
  */
class Function extends Evaluable with MetaCircuit {
  val nodes: HashMap[ID, Evaluable] = HashMap.empty
  val edges: HashMap[Edge, Edge]    = HashMap.empty

  val num_inputs:  Array[Int] = Array.fill(4)(0)
  val num_outputs: Array[Int] = Array.fill(4)(0)
  val last_inputs:  Array[Signal] = Array.fill(4)(Signal.empty(0))
  val last_outputs: Array[Signal] = Array.fill(4)(Signal.empty(0))

  override def set_input(dir: Direction, signal: Signal): Boolean = ???
  override def calc_outputs(): Unit = ???

  override def add(kv: (ID, Evaluable)): Boolean = ???
  override def connect(to: Edge, from: Edge): Boolean = ???
  override def remove(id: ID): Option[Evaluable] = ???
  override def disconnect(to: Edge, from: Edge): Boolean = ???
}
