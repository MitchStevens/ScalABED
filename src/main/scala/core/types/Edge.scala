package core.types

import core.types.ID.ID

import scalax.collection.GraphEdge.DiEdgeLike

/** An Edge is an object that is used to point to a port in a Function of Game.
  *
  * @constructor Create an edge from an id and a direction.
  * @param from
  * @param to
  */
class Edge(from: Side, to: Side) extends DiEdgeLike[ID] {
  override def isAt(pred: ID => Boolean): Boolean = pred (to.id)
  override def isAt[M >: ID](node: M): Boolean = ??? //(from eq node) || (to eq node)
  override def iterator: Iterator[ID] = Iterator(to.id)
  override def nodes: Product = ???
  override def isValidArity(size: Int): Boolean = size == 1
}

case class Side(id: ID, dir: Direction) extends Ordered[Side] {
  override def compare(that: Side): Int =
    (this.id compareTo that.id) * 4 + (this.dir compareTo that.dir)
}

