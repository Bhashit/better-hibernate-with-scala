package controllers

import java.util.{UUID, Collections}
import models._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import securesocial.core.RuntimeEnvironment
import services.UserService
import securesocial.core.SecureSocial
import javax.inject._
import play.api.i18n.{ MessagesApi, I18nSupport }
import utils.RequestUtil.toJsonString

@Singleton
class Application @Inject() (private val userService: UserService,
                             val messagesApi: MessagesApi,
                             implicit val env: RuntimeEnvironment[User]) extends Controller with SecureSocial[User] with I18nSupport {
  
  def index = Action { implicit request =>

    val u = new User
    val mainProfile = new UserProfile
    mainProfile.providerId = "twitter"
    mainProfile.userId = UUID.randomUUID.toString
    mainProfile.authMethod = "OAuth1"
    u.mainProfile = mainProfile
    val otherProfile = new UserProfile
    otherProfile.providerId = "github"
    otherProfile.userId = UUID.randomUUID.toString
    otherProfile.authMethod = "OAuth2"
    u.allProfiles.add(otherProfile)
    u.mainProfile.firstName = Option("Bhashit")
    u.mainProfile.email = Option("user@example.com")

    userService.save(u)

    Ok(views.html.message("A new User object was just saved"))
  }
}
