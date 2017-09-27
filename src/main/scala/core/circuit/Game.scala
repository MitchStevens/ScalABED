package core.circuit

import java.nio.file.DirectoryIteratorException

import core.types.ID.ID
import core.types.Signal.Signal
import core.types._
import main.scala.core.GameAction._

import scala.collection.mutable

/**
  * Created by Mitch on 4/23/2017.
  */
class Game(private var n: Int) {
  val mesa_circuit: MesaCircuit = new MesaCircuit
  val coord_map = mutable.Map.empty[Coord, EvaluableData]

  /**
  * 1. Add the evaluable to the MesaCircuit
  * 2. Connect all the edges in the MesaCircuit
  * 3. Evaluate the MesaCircuit
  * */
  def add(e: Evaluable, pos: Coord): Option[AddAction] =
    if (!coord_map.contains(pos)) {
      val id = ID.generate()
      mesa_circuit  += id -> e
      coord_map += pos -> EvaluableData(id, Direction.values(0))
      mesa_circuit connect_all (connections(pos):_*)
      mesa_circuit.evaluate()
      Some(AddAction(id, e.name, pos))
    } else None

  /**
  * 1. Disconnect all the edges in the MesaCircuit
  * 2. Remove the evaluable from the MesaCircuit
  * 3. Evaluate the MesaCircuit
  * */
  def remove(pos: Coord): Option[RemoveAction] =
    if (true) {
      val eval_data = coord_map(pos)
      val e = mesa_circuit.remove(eval_data.id)
      //function.
      coord_map -= pos
      Some(RemoveAction(eval_data.id, e.get.name, pos))
    } else None

  /**
  * 1. Disconnect all the edges
  * 2. Remove from MesaCircuit
  * 3. Rotate the Evaluable
  * 4. Add to Mesa Circuit
  * 5. Connect all the edges
  * 6. Evaluate
  * */
  def rotate(pos: Coord, rot: Direction): Option[RotateAction] =
    if (true) {
      val eval_data = coord_map(pos)
      mesa_circuit.disconnect(eval_data.id)
      val e = mesa_circuit.remove(eval_data.id).get
      coord_map += pos -> EvaluableData(eval_data.id, eval_data.rotation + rot)
      mesa_circuit += eval_data.id -> e
      this.mesa_circuit connect_all(connections(pos):_*)
      mesa_circuit.evaluate()
      Some(RotateAction(eval_data.id, e.name, pos, rot))
    } else None


  /**
  * 1. Disconnect all the edges
  * 2. Remove from MesaCircuit
  * 3. Move the Evaluable
  * 4. Add to Mesa Circuit
  * 5. Connect all the edges
  * 6. Evaluate
  * */
  def move(from: Coord, to: Coord): Option[MoveAction] =
    if (coord_map.contains(from) && !coord_map.contains(to)) {
      val eval_data = coord_map(from)
      mesa_circuit.disconnect(eval_data.id)
      val e = mesa_circuit(eval_data.id)
      coord_map += to -> coord_map(from)
      coord_map -= from
      mesa_circuit += eval_data.id -> e
      mesa_circuit.connect_all(connections(to):_*)
      mesa_circuit.evaluate()
      Some(MoveAction(eval_data.id, e.name, from, to))
    } else None

  private def connections(pos: Coord): Seq[(Side, Side)] = {
    def rel_dir(ev: EvaluableData, dir: Direction): Direction = ev.rotation - dir

    for {
      d   <- Direction.values
      ev1 <- coord_map.get(pos)
      ev2 <- coord_map.get(pos + d)
    } yield Side(ev1.id, rel_dir(ev1, d)) -> Side(ev2.id, rel_dir(ev2, d+2))
  }

  /*

  def reconnect(id: ID): Unit = {
    //abs_dir = rel_dir + rot
    def reconnect_dir(pos1: Coord, abs_dir: Direction): Unit =
      for {
        id1: ID <- game_info.id(pos1)
        id2: ID <- game_info.id(pos1 + abs_dir)
        rot1    <- game_info.rot(id1)
        rot2    <- game_info.rot(id2)
      } function.connect(
        Edge(id1, abs_dir - rot1),
        Edge(id2, abs_dir - rot2 + 2)
      )

    for {
      pos1 <- game_info.pos(id)
      abs_dir <- Direction.values
    } reconnect_dir(pos1, abs_dir)
  }

  def disconnect(id: ID): Unit = {

  }
  */

  def first_open: Option[Coord] =
    Coord.over_square(this.n).find(!coord_map.contains(_))

  def state: CircuitState = mesa_circuit.state

  case class EvaluableData(id: ID, rotation: Direction)
}
