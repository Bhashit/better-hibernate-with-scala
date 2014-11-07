package controllers

import models.User
import play.api.mvc.RequestHeader
import securesocial.controllers.BaseLoginPage
import securesocial.core.IdentityProvider
import securesocial.core.RuntimeEnvironment
import securesocial.core.services.RoutesService

// securesocial fuckers are making us override and instantiate the default
// controllers in order to provide an instance of RuntimeEnvironment. See the
// sample scala project in the securesocial repo.  
class LoginController(implicit override val env: RuntimeEnvironment[User]) extends BaseLoginPage[User]
