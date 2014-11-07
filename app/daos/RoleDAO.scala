package daos

import models.Role
import java.lang.Long
import org.hibernate.Session
import org.hibernate.criterion.Restrictions


class RoleDAO extends DAO[Role, Long] {
  def findByName(name: String)(implicit session: Session): Option[Role] = {
    val c = session.createCriteria(classOf[Role])
    c.add(Restrictions.eq(Role.Name, name))
    Option(c.uniqueResult().asInstanceOf[Role])
  }
}
