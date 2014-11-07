package models

import javax.persistence._
import java.lang.Long

@Entity
class Role(roleId: Option[Long] = None, roleName: String = null) extends PersistentEntity {
  def this() = {
    this(None)
  }

  id = roleId
  var name: String = roleName

  override def hashCode() = {
    113 * 71 + (if(name == null) 0 else name.hashCode)
  }

  override def equals(o: Any) = {
    o match {
      case that: Role => name == that.name
      case _ => false
    }
  }

}

object Role {
  val Name = "name"
  val UserRole = "ROLE_USER"
  val AdminRole = "ROLE_ADMIN"
}
