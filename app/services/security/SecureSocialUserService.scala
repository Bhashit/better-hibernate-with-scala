package services.security

import models._
import play.api.{Application, Logger}
import play.api.Logger.logger
import scala.concurrent.Future
import scaldi.Injectable.inject
import scaldi.Injector
import scaldi.play.ScaldiSupport
import securesocial.core.{BasicProfile, PasswordInfo}
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode, UserService => SecureUserService}
import services.UserService
import scala.concurrent.ExecutionContext.Implicits.global

class SecureSocialUserService(implicit ij: Injector) extends SecureUserService[User] {

  lazy val userService = inject [UserService]
  
  /** Gets called on login, registration or password-change. */
  override def save(profile: BasicProfile, mode: SaveMode): Future[User] =  {
    import SaveMode._
    mode match {
      case SignUp =>
        Future { userService.save(User(profile)) }
      case LoggedIn =>
        logger.info("User logged in")
        Future.successful(null)
      case _ => throw new AssertionError("Unexpected SaveMode")
    }
  }

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    Future {
      val user = userService.findByUserIdAndProviderId(userId, providerId)
      user.map(_.mainProfile).map(UserProfile.toBasicProfile)
    }
  }

  // TODO implement
  override def link(current: User, to: BasicProfile): Future[User]= ???

  // The following methods are only required if we allow
  // username-password based login.
  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = ???
  override def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = ???
  override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = ???
  override def saveToken(token: MailToken): Future[MailToken] = ???
  override def findToken(token: String): Future[Option[MailToken]] = ???
  override def deleteToken(uuid: String): Future[Option[MailToken]] = ???
  override def deleteExpiredTokens() = ???
}
