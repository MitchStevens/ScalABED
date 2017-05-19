package core.types

import core.types.ID.ID

/**
  * Created by Mitch on 5/13/2017.
  */
object Edge {
  case class Edge(val id: ID, val dir: Direction)
  def apply(id: ID, dir: Direction): Edge =
    new Edge(id, dir)
}
