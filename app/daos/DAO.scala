package daos

import scala.collection.mutable.Buffer
import org.hibernate.Session
import java.lang.reflect.ParameterizedType
import scala.collection.JavaConversions.asScalaBuffer
import java.util.{List => JList}
import models.PersistentEntity
import java.io.Serializable
import scala.collection.immutable
import persistence.query.Order
import org.hibernate.Criteria
import persistence.query.OrderBy
import persistence.query.Order
import org.hibernate.criterion.{Order => HibernateOrder}

abstract class DAO[T <: PersistentEntity, PK <: Serializable] {
  import DAO._
  
  protected val persistentClass: Class[T] = 
    getClass.getGenericSuperclass().asInstanceOf[ParameterizedType].
      getActualTypeArguments().apply(0).asInstanceOf[Class[T]]

  def findById(id: PK)(implicit s: Session): Option[T] = {
    Option(s.get(persistentClass, id).asInstanceOf[T]);
  }

  def findAll(ordering: OrderBy*)(implicit s: Session): Seq[T] = {
    val criteria = s.createCriteria(persistentClass)
    addOrders(criteria, ordering)
    criteria.list.asInstanceOf[JList[T]]
  }
  
  def removeById(id: PK)(implicit s: Session) = {
    s.delete(findById(id))
  }

  def remove(entity: T)(implicit s: Session) = {
    s.delete(entity)
  }

  def removeAll(implicit s: Session) = {
    // TODO do this via a query
    findAll().foreach(remove)
  }

  def save(entity: T)(implicit s: Session): T = {
    s.save(entity)
    entity
  }

  def save(entities: Iterable[T])(implicit s: Session) {
    // this could be very inefficient for large inserts as
    // hibernate will disable batch inserts for entities with
    // IDENTITY identifier generation strategy. Although, most
    // of the time, we won't be inserting large datasets.
    entities.map(s.save)
  }

  def update(entity: T)(implicit s: Session): T = {
    s.update(entity)
    entity
  }

  // TODO setup hibernate's batch-size property and use the batch-size value
  // here to flush the session every batch-size rows
  def update(entities: Iterable[T])(implicit s: Session) {
    entities.map(s.update)
  }
}

object DAO {
  private def toHibernateOrder(order: OrderBy) = {
    order match {
      case Order.asc(propertyName) => HibernateOrder.asc(propertyName)
      case Order.desc(propertyName) => HibernateOrder.desc(propertyName)
      case _ => throw new RuntimeException("invalid order type")
    }
  }
  
  private def addOrders(criteria: Criteria, ordering: Seq[OrderBy]) = {
    ordering map { toHibernateOrder  } foreach { criteria.addOrder }
  }
}
