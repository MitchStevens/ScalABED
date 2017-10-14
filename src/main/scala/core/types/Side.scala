package core.types

import core.types.ID.ID

object Side {
  type Side = (ID, Direction)

  def apply(id: ID, dir: Direction): Side = (id, dir)

  implicit class SideMethods(side: Side){
    def id: ID = side._1
    def dir: Direction = side._2
  }

}