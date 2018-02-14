package core.types

import core.circuit.{Game, MesaCircuit}
import core.types.Signal.Signal
import io.{FailedTest, LevelCompletion}

sealed trait GameTest extends (MesaCircuit => Either[LevelCompletion, MesaCircuit]) {

  override def equals(obj: scala.Any): Boolean = this match {
    case simple: SimpleGameTest => simple equals obj
  }

}

case class SimpleGameTest(inputs: Signal, outputs: Signal) extends GameTest {

  override def apply(mesa: MesaCircuit): Either[FailedTest, MesaCircuit] =
    if (mesa(inputs).flatten sameElements outputs)
      Right(mesa)
    else Left(FailedTest(this))

  override def equals(obj: Any): Boolean = obj match {
    case simple: SimpleGameTest => this.inputs == simple.inputs && this.outputs == simple.outputs
    case _                      => false
  }

}