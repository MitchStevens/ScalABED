package core.types

import core.circuit.{Game, MesaCircuit}
import core.types.Signal.Signal
import io.{FailedTest, LevelCompletion}

trait GameTest extends (MesaCircuit => Either[LevelCompletion, MesaCircuit])

class SimpleGameTest(inputs: Array[Signal], outputs: Array[Signal]) extends GameTest {

  override def apply(mesa: MesaCircuit): Either[FailedTest, MesaCircuit] =
    if (mesa(inputs) sameElements outputs)
      Right(mesa)
    else Left(FailedTest(this))

}
