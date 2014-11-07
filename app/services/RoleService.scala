package services

import models.Role
import java.lang.Long
import scaldi.Injector
import scaldi.Injectable.inject
import daos.RoleDAO
import models.Role
import daos.RoleDAO


class RoleService(implicit injector: Injector) extends BaseService[Role, Long] {
  protected val dao = inject [RoleDAO]

  def findByName(name: String): Option[Role] = {
    inTransaction { implicit session =>
      dao.findByName(name)
    }
  }
}
