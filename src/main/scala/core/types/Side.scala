package core.types

import core.types.ID.ID

case class Side(id: ID, dir: Direction) extends Ordered[Side] {
  override def compare(that: Side): Int =
    (this.id compareTo that.id) * 4 + (this.dir compareTo that.dir)
}

