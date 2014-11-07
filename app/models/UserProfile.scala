package models

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence._
import javax.persistence.{ Embeddable, Embedded }
import javax.persistence.Basic
import models.values.Address
import org.hibernate.annotations.Tuplizer
import org.hibernate.annotations.{ Target, Type }
import securesocial.core.AuthenticationMethod
import securesocial.core.BasicProfile
import securesocial.core.GenericProfile
import securesocial.core.OAuth1Info
import securesocial.core.OAuth2Info

@Embeddable
class UserProfile {

  @Basic(optional = false)
  var providerId: String = _

  @Basic(optional = false)
  var userId: String = _

  @Type(`type` = "string")
  var firstName: Option[String] = None

  @Type(`type` = "string")
  var lastName: Option[String] = None

  @Type(`type` = "string")
  var fullName: Option[String] = None

  @Type(`type` = "string")
  var email: Option[String] = None

  @Type(`type` = "string")
  var avatarUrl: Option[String] = None

  @Basic(optional = false)
  var authMethod: String = _

  @Embedded @Target(classOf[OAuth1])
  var oAuth1Info: Option[OAuth1] = None

  @Embedded @Target(classOf[OAuth2])
  var oAuth2Info: Option[OAuth2] = None

  override def equals(other: Any) = {
    other match {
      case that: UserProfile =>
        providerId == that.providerId && userId == that.userId
      case _ => false
    }
  }

  override def hashCode = {
    var hash = 71
    hash = hash * 17 + (if (providerId == null) 0 else providerId.hashCode)
    hash = hash * 17 + (if (userId == null) 0 else userId.hashCode)
    hash
  }
}

object UserProfile {

  val ProviderId = "providerId"
  val UserId = "userId"

  def apply(bp: BasicProfile): UserProfile = {
    val up = UserProfile(bp.userId, bp.providerId)
    up.firstName = bp.firstName
    up.lastName = bp.lastName
    up.fullName = bp.fullName
    up.email = bp.email
    up.avatarUrl = bp.avatarUrl
    up.authMethod = bp.authMethod.method
    up.oAuth1Info = bp.oAuth1Info map { OAuth1.apply }
    up.oAuth2Info = bp.oAuth2Info map { OAuth2.apply }
    up
  }

  // These two are supposed to be unique fields
  def apply(userId: String, providerId: String): UserProfile = {
    val up = new UserProfile
    up.providerId = providerId
    up.userId = userId
    up
  }

  def toBasicProfile(up: UserProfile) = {
    BasicProfile(
      up.providerId,
      up.userId,
      up.firstName,
      up.lastName,
      up.fullName,
      up.email,
      up.avatarUrl,
      AuthenticationMethod(up.authMethod),
      up.oAuth1Info map { OAuth1.toOAuth1Info },
      up.oAuth2Info map { OAuth2.toOAuth2Info }
    )
  }

}

@Embeddable
// The Access annotation needs to be present here for
// http://stackoverflow.com/a/18444590/1258479. For some reason,
// when an Embeddable (OAuth1) is part of another Embeddable (UserProfile) inside an
// ElementCollection (User.allProfiles), Hibernate resorts to using AccessType.PROPERTY.
// That leads to these two classes OAuth1 and OAuth2 having no properties.
// So, providing and explicit override. Same goes for OAuth2 as well.
@Access(AccessType.FIELD)
class OAuth1(
  var token: String = null,
  var secret: String = null
) {
  def this() = this(null)
}

object OAuth1 {
  def apply(oa1Info: OAuth1Info) = new OAuth1(oa1Info.token, oa1Info.secret)
  def toOAuth1Info(oa1: OAuth1) = OAuth1Info(oa1.token, oa1.secret)
}

// Putting all the vars in constructor params would require annotating each field
// with @scala.annotation.meta.field annotation for our hibernate (or any other)
// annotations to be applied to the corresponding field.
// http://www.scala-lang.org/api/current/index.html#scala.annotation.meta.package
@Embeddable
@Access(AccessType.FIELD)
class OAuth2 {
  var accessToken: String = null

  @Column(length = 50) @Type(`type` = "string")
  var tokenType: Option[String] = None

  @Type(`type` = "int")
  var expiresIn: Option[Int] = None

  @Type(`type` = "string")
  var refreshToken: Option[String] = None
}

object OAuth2 {
  def apply(oa: OAuth2Info) = {
    val res = new OAuth2
    res.accessToken = oa.accessToken
    res.tokenType = oa.tokenType
    res.expiresIn = oa.expiresIn
    res.refreshToken = oa.refreshToken;
    res
  }
  def toOAuth2Info(oa2: OAuth2) = OAuth2Info(oa2.accessToken, oa2.tokenType, oa2.expiresIn, oa2.refreshToken)
}
