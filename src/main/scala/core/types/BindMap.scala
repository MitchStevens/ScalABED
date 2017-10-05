package core.types

import scala.collection.mutable

/**
  * A BindMap is almost identical to a regular instance of mutable.Map, with the ability
  * to bind certain keys to other keys. For example, the following seqence of operations
  *
  *     bindmap bind (a -> b)
  *     bindmap += (b -> 42)
  *     println(bindmap.get(a))
  *
  *  would print Some(42), despite never actually setting any value for a in bindmap.
  */
class BindMap[A, B] extends mutable.Map[A, B] {
  private val m = mutable.HashMap.empty[A, Either[A, B]]

  override def get(key: A): Option[B] = m.get(key) match {
    case Some(Left(a))  => get(a)
    case Some(Right(b)) => Some(b)
    case None           => None
  }

  override def iterator: Iterator[(A, B)] =
    for (a <- m.keysIterator; b <- get(a))
      yield (a, b)

  override def +=(kv: (A, B)): BindMap.this.type = m.get(kv._1) match {
    case Some(Left(a))  => this
    case _              =>
      m += kv._1 -> Right(kv._2)
      this
  }

  override def -=(key: A): BindMap.this.type = {
    m -= key
    this
  }

  def bind(aa: (A, A)): Boolean =
    if (aa._1 == aa._2 || potential_cycle(aa))
      false
    else aa match {
      case (out, in) =>
        m += in -> Left(out)
        true
      case _ => false
    }

  //TODO:
  private def potential_cycle(aa: (A, A)): Boolean = false

  def unbind(child: A): Unit = {
    println(s"unbinding $child")
    m.get(child) match {
      case Some(Left(_))  => m -= child
      case _ => {println("not a child")}
    }
  }

  def num_binds(): Int = m.values.count {
    case Left(_)  => true
    case Right(_) => false
  }
}
