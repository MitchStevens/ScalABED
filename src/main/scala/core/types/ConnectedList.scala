package core.types

import scala.collection.mutable

/**
  * A ConnectedList is a list that has two values that are connected to one another. The size of this collection is thus
  * always even. This collection could be implemented by a list of tuples, but you couldn't perfrom the get opertion in
  * O(1) time
  *
  * Usage:
  *   m += a -> b
  *   print(m.get(a).adjacent) will print Some(b)
  *   print(m.get(b).adjacent) will print Some(a)
  *   print(m.get(a).parent)   will print None
  *   print(m.get(a).child)    will print Some(b)
  */
class ConnectedList[A] {
  private val m: mutable.Map[A, Node[A]] = mutable.Map.empty[A, Node[A]]

  def contains(elem: A): Boolean = m.contains(elem)
  def iterator: Iterator[A] = m.keysIterator

  def get(a: A): Option[Node[A]] = m.get(a)

  def +=(aa: (A, A)): Boolean =
    if (!m.contains(aa._1) && !m.contains(aa._2)) {
      val pc = kin_pair(aa)
      m += aa._1 -> pc._1
      m += aa._2 -> pc._2
      true
    } else false

  def -=(a: A): Boolean = get(a) match {
    case Some(b) =>
      m -= a
      m -= b.value()
      true
    case None => false
  }

  def connected(aa: (A, A)): Boolean = (get(aa._1), get(aa._2)) match {
    case (Some(Parent(_, c)), Some(Child(_, p))) => (aa._1 == p) && (aa._2 == c)
    case _ => false
  }

  def size: Int = m.size/2

  /*
  * You really shouldn't use null in Scala, throughout this code I've used Option or thrown an error. This use of null
  * is acceptable, since the reference to null is almost immediately changed to reference an actual object.
  * */
  private def kin_pair[A](aa: (A, A)): (Parent[A], Child[A]) = {
    val parent = Parent(aa._1, null)
    val child  = Child(aa._2, parent)
    parent._child = child
    (parent, child)
  }

}

sealed abstract class Node[AA](aa: AA) {
  def parent: Option[AA] = this match {
    case Child(_, parent) => Some(parent.value)
    case _                => None
  }

  def child: Option[AA] = this match {
    case Parent(_, child) => Some(child.value)
    case _                => None
  }

  def adjacent: Option[AA] = this match {
    case Parent(_, a) => Some(a.value)
    case Child (_, a) => Some(a.value)
    case _            => None
  }

  def value(): AA = this.aa
}
case class Parent[AA](aa: AA, var _child:  Child[AA])  extends Node[AA](aa)
case class Child [AA](aa: AA, var _parent: Parent[AA]) extends Node[AA](aa)