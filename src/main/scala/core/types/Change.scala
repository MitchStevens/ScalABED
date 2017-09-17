package core.types

import cats.Monad

/**
  * Created by Mitch on 8/24/2017.
  */
sealed trait Change[A]{

  def changed(): Changed[A] = this match {
    case Changed(a)   => Changed(a)
    case Unchanged(a) => Changed(a)
  }

}
  case class Changed[A](a: A)   extends Change[A]
  case class Unchanged[A](a: A) extends Change[A]

object Change {
  val changeMonad: Monad[Change] = new Monad[Change] {
    override def pure[A](a: A): Change[A] = Unchanged[A](a)
    override def flatMap[A, B](fa: Change[A])(f: A => Change[B]): Change[B] = fa match {
      case Unchanged(a) => f(a)
      case Changed(a)   => f(a).changed()
    }
    override def tailRecM[A, B](a: A)(f: A => Change[Either[A, B]]): Change[B] = f(a) match {
      case Unchanged(Left(a))  => tailRecM(a)(f)
      case Unchanged(Right(b)) => Unchanged(b)
      case Changed(Left(a))    => tailRecM(a)(f andThen (_.changed()))
      case Changed(Right(b))   => Unchanged(b)
    }
  }
}
