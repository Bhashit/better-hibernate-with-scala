package services

import models.Role
import java.lang.Long
import daos.RoleDAO
import models.Role
import daos.RoleDAO
import javax.inject._
import org.hibernate.SessionFactory

@Singleton
class RoleService @Inject() (protected val dao: RoleDAO,
    protected val sf: SessionFactory) extends BaseService[Role, Long](sf) {

  def findByName(name: String): Option[Role] = {
    inTransaction { implicit session =>
      dao.findByName(name)
    }
  }
}
