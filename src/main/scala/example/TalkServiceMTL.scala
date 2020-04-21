package example

import cats.MonadError
import cats.effect.Sync
import cats.implicits._
import example.TalkRepository.Talk

class TalkService[F[_]: Sync](talkRepository: TalkRepository[F])/*(implicit val me: MonadError[F, TalkError])*/ {

//    def addAndFindTalks(newTalk: Talk): F[List[Talk]] =
//        for {
//          _ <- talkRepository.save(newTalk)
//          talks <- talkRepository.find(newTalk.owner)
//        } yield talks

//  def addAndFindTalks2(newTalk: Talk): F[List[Talk]] = {
//    val talksForUser = for {
//      _ <- talkRepository.save(newTalk)
//      talks <- talkRepository.find(newTalk.owner)
//    } yield talks
//
//    me.handleErrorWith(talksForUser) {
//      case OffensiveError(_) => me.pure(List())
//    }

    def addAndFindTalks2(newTalk: Talk): F[List[Talk]] = {
      val talksForUser = for {
        _ <- Sync[F].delay(println("some log"))
        _ <- talkRepository.save(newTalk)
        talks <- talkRepository.find(newTalk.owner)
      } yield talks

      Sync[F].handleErrorWith(talksForUser) {
        case OffensiveError(_) => Sync[F].pure(List())
      }

  }
}

class TalkServiceMTL[F[_]](talkRepository: TalkRepository[F])(implicit F: Sync[F], talkErrorHandler: TalkErrorHandler[F, TalkError]) {

  private def addAndFindTalks3(newTalk: Talk): F[List[Talk]] = {
    for {
      _ <- talkRepository.save(newTalk)
      talks <- talkRepository.find(newTalk.owner)
    } yield talks
  }

  def businessLogic(newTalk: Talk) =
    makeARemoteLogging("logging") *>
      talkErrorHandler.handle(addAndFindTalks3(newTalk))

  private def makeARemoteLogging(text: String) = F.delay(println(text))
}
