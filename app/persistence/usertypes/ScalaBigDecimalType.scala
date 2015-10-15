package persistence
package usertypes

import _root_.org.hibernate
import hibernate.usertype.UserType
import java.io.Serializable
import java.sql.{Types, ResultSet, PreparedStatement}
import hibernate.engine.spi.SessionImplementor
import hibernate.Hibernate

/**
  * Persist scala enums as ints
  */
class ScalaBigDecimalType extends UserType {

  override def sqlTypes() =  Array(Types.NUMERIC)

  override def returnedClass = classOf[BigDecimal]

  override def equals(x: Object, y: Object): Boolean =  x == y

  override def hashCode(x: Object) = x.hashCode()

  override def nullSafeGet(resultSet: ResultSet, names: Array[String],
      session: SessionImplementor, owner: Object): Object = {
    val value = resultSet.getBigDecimal(names(0))
    if (resultSet.wasNull()) return null
    else {
      return scala.math.BigDecimal(value)
    }
  }
  override def nullSafeSet(statement: PreparedStatement, value: Object,
      index: Int, session: SessionImplementor): Unit = {
    if (value == null) {
      statement.setNull(index, Types.INTEGER)
    } else {
      val bd = value.asInstanceOf[BigDecimal]
      statement.setBigDecimal(index, bd.bigDecimal)
    }
  }

  override def deepCopy(value: Object): Object = value
  override def isMutable() = false
  override def disassemble(value: Object) = value.asInstanceOf[Serializable]
  override def assemble(cached: Serializable, owner: Object): Object = cached.asInstanceOf[Object]
  override def replace(original: Object, target: Object, owner: Object) = original
}
