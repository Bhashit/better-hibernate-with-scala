package controllers

import java.util.Collections
import java.util.Date
import java.util.UUID
import models._
import models.User
import play.api._
import play.api.mvc._
import scaldi.Injectable.inject
import scaldi.Injector
import securesocial.core.RuntimeEnvironment
import models.UserProfile
import services.UserService
import securesocial.core.SecureSocial

class Application(implicit ij: Injector) extends Controller with SecureSocial[User] {

  private val userService = inject [UserService]
  override implicit lazy val env = inject[RuntimeEnvironment[User]]

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




