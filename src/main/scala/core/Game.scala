package core

import javafx.beans.InvalidationListener

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 3/18/2017.
  */

class Game extends MetaCircuit {
  import MetaCircuit._

  val n: Int = 0
  val circuits: HashMap[Coord, Evaluable] = HashMap.empty
  val nodes: HashMap[ID, Evaluable] = HashMap.empty
  val edges: HashMap[Edge, Edge] = HashMap.empty

  def apply(pos: Coord): Option[Evaluable] = ???

  override def add(kv: (ID, Evaluable)): Boolean = ???
  override def remove(id: ID) = ???
  def rotate(id: ID, rot: Int): Boolean = ???
  def move(pos1: Coord, pos2: Coord): Boolean = ???

  override def connect(to: Edge, from: Edge) = ???
  override def disconnect(to: Edge, from: Edge) = ???

  def next_open(): Option[Coord] =
    Coord.over_square(n) find (circuits.contains)

  override def receive = {
    case "eval" => {}
  }
}