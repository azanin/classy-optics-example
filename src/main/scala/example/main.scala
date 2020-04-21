package example

import cats.effect.{ExitCode, IO, IOApp}
import com.olegpy.meow.hierarchy._
import example.TalkRepository.Talk

object main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val talkErrorHandler: LiveTalkErrorHandler[IO] =
      new LiveTalkErrorHandler

    val comp = for {
      repo <- TalkRepository.instance[IO]
      service <- IO(new TalkServiceMTL[IO](repo))
      _ <- service.businessLogic(Talk("pippo", "pippo", "me"))
      _ <- service.businessLogic(Talk("pluto", "pluto", "me"))
      talks <- service.businessLogic(Talk("paperino", "paperino", "me"))
      _ <- IO.delay(println(talks))
    } yield ()

    comp.as(ExitCode.Success)
  }
}
