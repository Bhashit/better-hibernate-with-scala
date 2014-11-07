package setup

import java.lang.reflect.Constructor
import models._
import play.api.Application
import play.api.GlobalSettings
import scala.collection.immutable.ListMap
import scaldi.play.ControllerInjector
import scaldi.play.ScaldiSupport
import securesocial.controllers.ViewTemplates
import securesocial.core.RuntimeEnvironment
import setup.modules.{PersistenceModule, WebModule}
import securesocial.core.providers.FacebookProvider
import securesocial.core.providers.GitHubProvider
import securesocial.core.providers.GoogleProvider
import securesocial.core.providers.TwitterProvider
import services.{UserService, RoleService}
import services.security.SecureSocialUserService
import scaldi.Injectable.inject

object Global extends GlobalSettings with ScaldiSupport {

  override def beforeStart(app: Application) {
    // Inject our application object into our custom property-accessor.
    // The property accessor needs to get Class[_] objects for our entity and
    // component classes. And for that, it needs the correct class-loader.
    // That class loader can be accessed from the play application object.
    _root_.persistence.property.ScalaPropertyAccessor.playApplication = Option(app)
  }

  override def onStart(app: Application) {
    super.onStart(app) // this will initialize the scalid injector too
    insertRolesInDB()
  }

  private def insertRolesInDB() {
    val roleService = inject [RoleService]
    val ur = new Role(Option(Long.box(1)), Role.UserRole)
    val ar = new Role(Option(Long.box(2)), Role.AdminRole)

    roleService findByName(Role.UserRole) match {
      case None => roleService.save(ur)
      case _ =>
    }

    roleService findByName(Role.AdminRole) match {
      case None => roleService.save(ar)
      case _ =>
    }
  }

  implicit val secureSocialRuntimeEnvironment: RuntimeEnvironment[User] = new RuntimeEnvironment.Default[User] {
    override lazy val userService = new SecureSocialUserService

    override lazy val providers = ListMap(
      include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
      include(new GitHubProvider(routes, cacheService, oauth2ClientFor(GitHubProvider.GitHub))),
      include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
      include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter)))
    )

    override lazy val viewTemplates: ViewTemplates = new views.security.SecureSocialViewTemplates
  }

  override def applicationModule = new PersistenceModule :: new WebModule

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[User]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(secureSocialRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }
}
