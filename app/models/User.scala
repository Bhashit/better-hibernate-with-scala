package models

import java.util.{HashSet, Set => JSet, LinkedHashSet}
import javax.persistence._
import models.values.Address
import org.hibernate.annotations.{Tuplizer, Target}
import scala.collection.JavaConversions.asScalaSet
import scala.collection.immutable.Set
import securesocial.core.{BasicProfile, GenericProfile}


@Entity
@Table(name = "app_user", uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("providerId", "userId"))))
class User extends PersistentEntity {

  @Embedded
  var mainProfile: UserProfile = _

  @Embedded @Target(classOf[Address])
  var address: Option[Address] = None

  @ElementCollection(fetch = FetchType.EAGER)
  var allProfiles: JSet[UserProfile] = new HashSet
   
  @ManyToMany(fetch = FetchType.EAGER)
  private var _roles: JSet[Role] = new HashSet

  def roles = asScalaSet(_roles)
  def addRole(role: Option[Role]) = role foreach (roles += _)

  override def hashCode = {
    79 * 31 + (if(mainProfile == null) 0 else mainProfile.hashCode)
  }

  override def equals(o: Any) = {
    o match {
      case that: User => mainProfile == that.mainProfile
      case _ => false  
    }
  }

  override def toString = {
    s"""User[id: ${id}]"""
  }
}

object User {
  val MainProfile = "mainProfile"
  val Role = "role"

  def apply(bp: BasicProfile) = {
    val u = new User
    u.mainProfile = UserProfile(bp)
    u
  }
}
