package setup.modules

import models.User
import scala.collection.immutable.ListMap
import daos.{UserDAO, RoleDAO}
import securesocial.controllers.BaseLoginPage
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.FacebookProvider
import securesocial.core.providers.GitHubProvider
import securesocial.core.providers.GoogleProvider
import securesocial.core.providers.TwitterProvider
import services.{UserService, RoleService}
import services.security.SecureSocialUserService


import com.google.inject.{Provides, AbstractModule, TypeLiteral}
import com.google.inject.matcher.Matchers
import com.google.inject.spi._
import javax.inject._
// import play.api.inject._
import play.api._
import play.api.i18n.MessagesApi


class WebModule extends AbstractModule {
  def configure() = {
    val typeLiteral = new TypeLiteral[RuntimeEnvironment[User]]() {}
    bind(typeLiteral).to(classOf[SecureSocialRuntimeEnvironment])
  }
}

@Singleton
class SecureSocialRuntimeEnvironment @Inject() (override val userService: SecureSocialUserService,
    implicit val messagesApi: MessagesApi) extends RuntimeEnvironment.Default[User] {

  private implicit val re: RuntimeEnvironment[User] = this

  override lazy val providers = ListMap(
    include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
    include(new GitHubProvider(routes, cacheService, oauth2ClientFor(GitHubProvider.GitHub))),
    include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
    include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter)))
  )
}
