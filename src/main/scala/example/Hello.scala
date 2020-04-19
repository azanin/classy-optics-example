package example

import cats.MonadError
import cats.effect.{ExitCode, IO, IOApp}
import com.olegpy.meow.hierarchy._

import scala.util.Random // All you need is this import!

object Hello extends IOApp {

  sealed trait BusinessError extends Throwable
  case object A extends BusinessError
  case class B(description: String) extends BusinessError

  def handleError[A](effect: IO[A], fallback: IO[A])(implicit ex: MonadError[IO, BusinessError]) =
    effect.handleErrorWith(t => IO.delay(println(t)) *> fallback)

  override def run(args: List[String]): IO[ExitCode] = {
    val io = IO(Random.nextInt(2)).flatMap { case 1 => IO.raiseError(new Exception("boom")) }
    val value: IO[Int] = handleError(io, IO(10))

    value.map(println)as(ExitCode.Success)
  }
}
