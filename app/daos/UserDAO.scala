package daos

import models.User
import models.User.MainProfile
import java.lang.Long
import org.hibernate.Session
import org.hibernate.criterion.Restrictions.{eq => equal, _}
import models.UserProfile
import models.UserProfile._


class UserDAO extends DAO[User, Long] {
  def findByUserIdAndProviderId(userId: String, providerId: String)(implicit session: Session): Option[User] = {
    val c = session.createCriteria(classOf[User])
    c.add(equal(s"$MainProfile.$UserId", userId))
    c.add(equal(s"$MainProfile.$ProviderId", providerId))
    Option(c.uniqueResult().asInstanceOf[User])
  }
}
