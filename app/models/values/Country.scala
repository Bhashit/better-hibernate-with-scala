package models
package values

import javax.persistence._
import org.hibernate.annotations.{ Fetch, FetchMode }
import java.util.LinkedHashSet
import java.util.{HashSet, Set => JSet}
import scala.collection.JavaConversions.asScalaSet
import scala.collection.immutable.Set


@Entity
class Country extends PersistentEntity {

  var name: String = _

  var code: String = _

  override def equals(other: Any) = {
    other match {
      case that: Country => {
        that.code == code
      }
      case _ => false
    }
  }

  override def hashCode = {
    Option(code).map(_.hashCode).getOrElse(0)
  }
}

object Country {
  val Name = "name"
  val Code = "code"

  def apply(code: String, name: String) = {
    val country = new Country
    country.code = code
    country.name = name
    country
  }
}
