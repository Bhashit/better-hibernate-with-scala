package persistence.usertypes

import java.{io => jo}
import java.sql.{PreparedStatement, ResultSet}
import org.hibernate.`type`.AbstractSingleColumnStandardBasicType
import org.hibernate.`type`.StandardBasicTypes._
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

object AbstractOptionType {
  val scalaToHibernateTypes: Map[Class[_], AbstractSingleColumnStandardBasicType[_]] =
    Map(
      classOf[Integer] -> INTEGER,
      classOf[Long] -> LONG,
      classOf[Float] -> FLOAT,
      classOf[Double] -> DOUBLE,
      classOf[Boolean] -> BOOLEAN
    )
}

abstract class AbstractOptionType[T](theClass: Class[T]) extends UserType {
  import AbstractOptionType._

  def inner: AbstractSingleColumnStandardBasicType[_] = scalaToHibernateTypes(theClass)

  def sqlTypes = Array(inner.sqlType)

  def returnedClass = classOf[Option[T]]

  final def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Object) = {
    val x = inner.nullSafeGet(rs, names, session, owner)
    if (x == null) None else Some(x)
  }

  final def nullSafeSet(ps: PreparedStatement, value: Object, index: Int, session: SessionImplementor) = {
    inner.nullSafeSet(ps, value.asInstanceOf[Option[_]].getOrElse(null), index, session)
  }

  def isMutable = false

  def equals(x: Object, y: Object) = x == y

  def hashCode(x: Object) = x.hashCode

  def deepCopy(value: Object) = value

  def replace(original: Object, target: Object, owner: Object) = original

  def disassemble(value: Object) = value.asInstanceOf[jo.Serializable]

  def assemble(cached: jo.Serializable, owner: Object): Object = cached.asInstanceOf[Object]
}
