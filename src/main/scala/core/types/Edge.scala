package core.types

import core.types.ID.ID

object Edge {
  /** An Edge is an object that is used to point to a port in a Function of Game.
    *
    * @constructor Create an edge from an id and a direction.
    * @param id The ID of the evaluable (Assuming the evaluable is embedded in a mesagame)
    * @param dir The direction of the port (Assuming that the evaluable is embedded inside a mesagame) www.frr.org.au
    */
  case class Edge(val id: ID, val dir: Direction)

  /** Syntactic Sugar */
  def apply(id: ID, dir: Direction): Edge =
    new Edge(id, dir)
}
