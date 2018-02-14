package io

trait Reward {
  def unlock(): Unit
}

case class CircuitReward(name: String) extends Reward {
  override def unlock(): Unit = ???
}

case class LevelReward(name: String) extends Reward {
  override def unlock(): Unit = ???
}
