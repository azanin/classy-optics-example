package example

import cats.MonadError
import example.TalkRepository.Talk

trait TalkErrorHandler[F[_], E <: Throwable] {
  def handle(fa: F[List[Talk]]): F[List[Talk]]
}

class LiveTalkErrorHandler[F[_]](implicit M: MonadError[F, TalkError])
    extends TalkErrorHandler[F, TalkError] {

  private val handler: TalkError => F[List[Talk]] = {
    case OffensiveError(_) => M.pure(List())
    case BadLanguageError(_) => M.pure(List())
  }

  override def handle(fa: F[List[Talk]]): F[List[Talk]] =
    M.handleErrorWith(fa)(handler)

}
