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
class Game(private var n: Int) extends mutable.Map[Coord, Evaluable] {
  val mesa_circuit: MesaCircuit = new MesaCircuit
  val coord_map = mutable.Map.empty[Coord, EvaluableData]

  override def get(key: Coord): Option[Evaluable] = coord_map.get(key).map(_.id).flatMap(mesa_circuit.get)
  override def iterator: Iterator[(Coord, Evaluable)] =
    for (pos <- coord_map.keysIterator)
      yield (pos, this(pos))
  override def +=(kv: (Coord, Evaluable)): Game.this.type = {
    add_evaluable(kv._2, kv._1)
    this
  }
  override def -=(key: Coord): Game.this.type = {
    remove_evaluable(key)
    this
  }
  override def size: Int = coord_map.size

  def board_size: Int = n

  /**
  * 1. Add the evaluable to the MesaCircuit
  * 2. Connect all the edges in the MesaCircuit
  * 3. Evaluate the MesaCircuit
  * */
  def add_evaluable(e: Evaluable, pos: Coord): Option[AddAction] =
    if (!coord_map.contains(pos)) {
      val id = ID.generate()
      mesa_circuit += id -> e
      coord_map += pos -> EvaluableData(id)
      val connections = connect_sides(pos)
      mesa_circuit.evaluate()
      Some(AddAction(id, e.name, pos, connections))
    } else None

  /**
  * 1. Disconnect all the edges in the MesaCircuit
  * 2. Remove the evaluable from the
    * MesaCircuit
  * 3. Evaluate the MesaCircuit
  * */
  def remove_evaluable(pos: Coord): Option[RemoveAction] =
    if (true) {
      val eval_data = coord_map(pos)
      val disconnections = this.disconnect_sides(pos)
      val e = mesa_circuit.remove(eval_data.id)
      coord_map -= pos
      Some(RemoveAction(eval_data.id, e.get.name, pos, disconnections))
    } else None

  /**
  * 1. Disconnect all the edges
  * 3. Rotate the Evaluable
  * 5. Connect all the edges
  * 6. Evaluate
  * */
  def rotate_evaluable(pos: Coord, rot: Direction): Option[RotateAction] =
    if (coord_map.contains(pos)) {
      val eval_data = coord_map(pos)
      val disconn = this.disconnect_sides(pos)
      val name = mesa_circuit(eval_data.id).name
      coord_map += pos -> EvaluableData(eval_data.id, eval_data.rotation + rot)
      val conn = this.connect_sides(pos)
      mesa_circuit.evaluate()
      Some(RotateAction(eval_data.id, name, pos, rot, conn, disconn))
    } else None


  /**
    * Disconnect all the edges
    * Remove from the MesaCircuit
    * Move the Evaluable
    * Add to the MesaCircuit
    * Connect all the edges
    * Evaluate
    */
  def move_evaluable(from: Coord, to: Coord): Option[MoveAction] =
    if (coord_map.contains(from) && !coord_map.contains(to)) {
      val eval_data = coord_map(from)
      val name = mesa_circuit(eval_data.id).name
      val disconn = this.disconnect_sides(from)
      coord_map += to -> eval_data
      coord_map -= from
      val conn = this.connect_sides(to)
      mesa_circuit.evaluate()
      Some(MoveAction(eval_data.id, name, from, to, conn, disconn))
    } else None

  def toggle_evaluable(pos: Coord, a: Any): Option[ToggleAction] =
    if (coord_map.contains(pos)) {
      val eval_data = coord_map(pos)
      val e = mesa_circuit(eval_data.id)
      e.toggle(a)
      Some(ToggleAction(eval_data.id, e.name, pos, a))
    } else None

  /**
    * */
  private def connect_sides(pos: Coord): mutable.ArrayBuffer[Coord] = {
    val adj = mutable.ArrayBuffer.empty[Coord]
    for {
      ev1 <- coord_map.get(pos)
      dir <- Direction.values
      ev2 <- coord_map.get(pos + dir)
    } {
      val result = mesa_circuit.connect(Side(ev1.id, dir - ev1.rotation) -> Side(ev2.id, dir + 2 - ev2.rotation))
      if (result)
        adj += pos + dir
    }
    adj
  }

  private def disconnect_sides(pos: Coord): mutable.ArrayBuffer[Coord] = {
    val adj = mutable.ArrayBuffer.empty[Coord]
    for {
      ev1  <- coord_map.get(pos)
      dir <- Direction.values
      ev2  <- coord_map.get(pos + dir)
    } {
      val result = mesa_circuit.disconnect(Side(ev1.id, dir - ev1.rotation) -> Side(ev2.id, dir + 2 - ev2.rotation))
      if (result)
        adj += pos + dir
    }
    adj
  }

  def first_open: Option[Coord] = {
    Coord.over_square(this.n).find(!coord_map.contains(_))
  }

  def state: CircuitState = mesa_circuit.state

  case class EvaluableData(id: ID, rotation: Direction = Direction.values(0))
}
