package services

import models._
import java.lang.Long
import scaldi.Injector
import scaldi.Injectable.inject
import daos.UserDAO
import daos.RoleDAO
import models.Role
import java.util.HashSet
import java.util.Arrays

class UserService(implicit injector: Injector) extends BaseService[User, Long] {
  protected val dao = inject [UserDAO]
  // TODO experiment to see what happens if the inTransaction calls are nested
  // Do so by using RoleService instead of RoleDAO here.
  private val roleDao = inject [RoleDAO]

  // When a user is saved, we specify a default role for them.
  override def save(user: User) = {
    inTransaction { implicit session =>
      val defaultRole = roleDao.findByName(Role.UserRole)
      user.addRole(defaultRole)
      super.save(user)
    }
  }

  def findByUserIdAndProviderId(userId: String, providerId: String): Option[User] = {
    inTransaction { implicit sess => dao.findByUserIdAndProviderId(userId, providerId)  }
  }
}
