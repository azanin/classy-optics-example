package example

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import example.TalkRepository.Talk

trait TalkRepository[F[_]] {
  def find(owner: String): F[List[Talk]]
  def save(talk: Talk): F[Unit]
}

object TalkRepository {
  case class Talk(description: String, title: String, owner: String)

  def instance[F[_]: Sync]: F[TalkRepository[F]] = {
    Ref
      .of[F, Map[String, List[Talk]]](Map())
      .map(ref => new FakeTalkRepository[F](ref))
  }

  class FakeTalkRepository[F[_]](state: Ref[F, Map[String, List[Talk]]])(
    implicit F: Sync[F]
  ) extends TalkRepository[F] {
    override def find(owner: String): F[List[Talk]] = {
      state.get.map(_.get(owner).toList.flatten)
    }

    override def save(talk: Talk): F[Unit] =
      validateTalk(talk) *>
        find(talk.owner)
          .flatMap(talks => state.update(_.updated(talk.owner, talks :+ talk)))

    private def validateTalk(talk: Talk): F[Unit] =
      if (talk.description.contains("object oriented is cool"))
        F.raiseError(OffensiveError("bleah"))
      else if (talk.description.contains("english"))
        F.raiseError(BadLanguageError(""))
      else F.unit
  }

}

sealed trait TalkError extends Exception
case class OffensiveError(description: String) extends TalkError
case class BadLanguageError(description: String) extends TalkError
