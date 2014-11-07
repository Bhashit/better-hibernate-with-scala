package views.security

import play.api.data.Form
import play.api.i18n.Lang
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import securesocial.controllers.ViewTemplates
import securesocial.core.RuntimeEnvironment

class SecureSocialViewTemplates(implicit env: RuntimeEnvironment[_]) extends ViewTemplates.Default(env) {
  override def getLoginPage(
    form: Form[(String, String)],
    msg: Option[String] = None
  )(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.security.login(msg)(request.asInstanceOf[Request[AnyContent]], lang, env)
  }
}
