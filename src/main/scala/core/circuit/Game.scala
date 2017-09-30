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
      connect_sides(pos)
      mesa_circuit.evaluate()
      Some(AddAction(id, e.name, pos))
    } else None

  /**
  * 1. Disconnect all the edges in the MesaCircuit
  * 2. Remove the evaluable from the MesaCircuit
  * 3. Evaluate the MesaCircuit
  * */
  def remove_evaluable(pos: Coord): Option[RemoveAction] =
    if (true) {
      val eval_data = coord_map(pos)
      val e = mesa_circuit.remove(eval_data.id)
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
  def rotate_evaluable(pos: Coord, rot: Direction): Option[RotateAction] =
    if (coord_map.contains(pos)) {
      val eval_data = coord_map(pos)
      this.disconnect_sides(pos)
      val e = mesa_circuit.remove(eval_data.id).get
      coord_map += pos -> EvaluableData(eval_data.id, eval_data.rotation + rot)
      mesa_circuit += eval_data.id -> e
      this.connect_sides(pos)
      mesa_circuit.evaluate()
      Some(RotateAction(eval_data.id, e.name, pos, rot))
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
      val e = mesa_circuit(eval_data.id)
      this.disconnect_sides(from)
      mesa_circuit -= eval_data.id
      coord_map += to -> eval_data
      coord_map -= from
      mesa_circuit += eval_data.id -> e
      this.connect_sides(to)
      mesa_circuit.evaluate()
      Some(MoveAction(eval_data.id, e.name, from, to))
    } else None

  def toggle_evaluable(pos: Coord, a: Any): Option[ToggleAction] =
    if (coord_map.contains(pos)) {
      val eval_data = coord_map(pos)
      val e = mesa_circuit(eval_data.id)
      println(e.getClass)
      e.toggle(a)
      Some(ToggleAction(eval_data.id, e.name, pos, a))
    } else None

  /**
    * */
  private def connect_sides(pos: Coord): TraversableOnce[ConnectionAction] = {
    def rel_dir(ev: EvaluableData, dir: Direction): Direction = dir - ev.rotation

    val actions = mutable.ArrayBuffer.empty[ConnectionAction]
    for {
      d   <- Direction.values
      ev1 <- coord_map.get(pos)
      ev2 <- coord_map.get(pos + d)
    } {
      val result = mesa_circuit.connect(Side(ev1.id, rel_dir(ev1, d)) -> Side(ev2.id, rel_dir(ev2, d+2)))
      if (result)
        actions += ConnectionAction(pos, pos + d)
    }
    actions
  }

  private def disconnect_sides(pos: Coord): TraversableOnce[DisconnectionAction] = {
    val actions = mutable.ArrayBuffer.empty[DisconnectionAction]
    for (ev <- coord_map.get(pos); dir <- Direction.values) {
      val result = mesa_circuit.disconnect(Side(ev.id, dir))
      if (result)
        actions += DisconnectionAction(pos, pos + dir)
    }
    actions
  }

  def first_open: Option[Coord] = {
    Coord.over_square(this.n).find(!coord_map.contains(_))
  }

  def state: CircuitState = mesa_circuit.state

  case class EvaluableData(id: ID, rotation: Direction = Direction.values(0))
}
