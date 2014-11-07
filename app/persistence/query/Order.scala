package persistence.query

sealed trait OrderBy {
  val propertyName: String
}

object Order {
  case class asc(propertyName: String) extends OrderBy
  case class desc(propertyName: String) extends OrderBy
}
