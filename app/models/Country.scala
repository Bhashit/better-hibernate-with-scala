package models

import javax.persistence._

@Entity
class Country extends PersistentEntity {
  var name: String = _
  var code: String = _

  override def equals(other: Any) = {
    if(other == null || !other.isInstanceOf[Country]) {
      false
    }
    val otherCountry = other.asInstanceOf[Country]
    otherCountry.code == this.code
  }

  override def hashCode = {
    if(code == null) 0 else code.hashCode
  }
}
