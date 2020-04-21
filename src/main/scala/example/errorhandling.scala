package example

import cats.MonadError
import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.olegpy.meow.hierarchy._
import example.errors.NotInterestingTalk

object errorhandling extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val program1: IO[Unit] = IO.raiseError(NotInterestingTalk)
    //   val program2: IO[Unit] = IO.raiseError(new Exception("Boom"))

    //val handled = errors.ioSpecificHandle(program1)

    val generalHandled: IO[Unit] = errors.generalHandle(program1, errors.talkErrorHandler)

    //   val letExpoled = errors.generalHandle(program2, errors.talkErrorHandler)

    generalHandled.as(ExitCode.Success)
  }
}

object errors {
  sealed trait TalkError extends Exception
  case object NotInterestingTalk extends TalkError
  case object AlreadyTakenTalk extends TalkError

  def talkErrorHandler[A](error: TalkError): IO[Unit] = error match {
    case NotInterestingTalk => IO.delay(println("Not interesting talk"))
    case AlreadyTakenTalk   => IO.delay(println("Already Taken talk"))
  }

  def ioSpecificHandle(
    fa: IO[Unit]
  )(implicit me: MonadError[IO, TalkError]): IO[Unit] = {
    me.handleErrorWith(fa)(talkErrorHandler)
  }

  def generalHandle[F[_]: Sync, A, E <: Throwable](fa: F[A], handler: E => F[A])(
    implicit me: MonadError[F, E]
  ) = {
    me.handleErrorWith(fa)(handler)
  }
}
