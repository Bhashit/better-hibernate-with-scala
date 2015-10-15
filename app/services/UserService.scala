package services

import models._
import java.lang.Long
import daos.UserDAO
import daos.RoleDAO
import models.Role
import java.util.HashSet
import java.util.Arrays
import javax.inject._
import org.hibernate.SessionFactory

// TODO experiment to see what happens if the inTransaction calls are neste
// Do so by using RoleService instead of RoleDAO here.

@Singleton
class UserService @Inject() (protected val dao: UserDAO,
    private val roleDao: RoleDAO,
    protected val sf: SessionFactory) extends BaseService[User, Long](sf) {
  // When a user is saved, we specify a default role for them.
  override def save(user: User) = {
    inTransaction { implicit session =>
      val defaultRole = roleDao.findByName(Role.UserRole)
      defaultRole foreach (user.roles += _)
      super.save(user)
    }
  }

  def findByUserIdAndProviderId(userId: String, providerId: String): Option[User] = {
    inTransaction { implicit sess => dao.findByUserIdAndProviderId(userId, providerId)  }
  }
}
