package core

trait Paintable[T] {
  def repaint(t: T): Unit
}
