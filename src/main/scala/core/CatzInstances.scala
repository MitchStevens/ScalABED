package core

import cats.{Functor, Monoid, MonoidK}

/**
  * Created by Mitch on 9/2/2017.
  */
object CatzInstances {

  /*
  * The first and last monoid instance are copies of the First and Last Maybe wrappers as defined in the Data.Monoid
  * package. These instances allow you to reduce lists of Options to get the first or last in a list
  *
  * x first y == y last x
  *
  * https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Monoid.html
  * */
  implicit val first_monoid = new MonoidK[Option] {
    override def empty[A]: Option[A] = None
    override def combineK[A](x: Option[A], y: Option[A]): Option[A] =
      x first y
  }

  implicit val last_monoid = new MonoidK[Option] {
    override def empty[A]: Option[A] = None
    override def combineK[A](x: Option[A], y: Option[A]): Option[A] =
      x last y
  }

  implicit class OptionMethods[A](opt1: Option[A]) {
    def first(opt2: Option[A]): Option[A] =
      if (opt1.isDefined) opt1 else opt2

    def last(opt2: Option[A]): Option[A] =
      if (opt2.isDefined) opt2 else opt1
  }

}
