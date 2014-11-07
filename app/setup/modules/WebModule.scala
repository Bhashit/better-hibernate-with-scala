package setup.modules

import models.User
import scala.collection.immutable.ListMap
import scaldi.Module
import daos.{UserDAO, RoleDAO}
import securesocial.controllers.BaseLoginPage
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.FacebookProvider
import securesocial.core.providers.GitHubProvider
import securesocial.core.providers.GoogleProvider
import securesocial.core.providers.TwitterProvider
import services. {UserService, RoleService}
import services.security.SecureSocialUserService
import controllers.LoginController

class WebModule(implicit private val env: RuntimeEnvironment[User]) extends Module {

  binding to new controllers.Application
  binding to new LoginController

  binding to new UserDAO
  binding to new UserService

  binding to new RoleDAO
  binding to new RoleService

  bind [RuntimeEnvironment[User]] to env

}
