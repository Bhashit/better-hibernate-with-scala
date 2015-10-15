package models.values

import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
class Address {
  var street: String = _
  var city: String = _
  var state: String = _

  @ManyToOne
  var country: Country = _
}
