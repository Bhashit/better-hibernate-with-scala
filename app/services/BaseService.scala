package services

import daos.DAO
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import java.io.Serializable
import scala.collection.immutable
import persistence.query.Order
import persistence.query.OrderBy
import models.PersistentEntity

// TODO try to find a way that the service classes can call methods from other
// service classes directly. Currently, the with nested inTransaction calls,
// everytime a service method exits, a transaction commit occurs.
abstract class BaseService[T <: PersistentEntity, PK <: Serializable](protected val sessionFactory: SessionFactory) {
  protected def dao: DAO[T, PK];

  def inTransaction[RES](func: Session => RES): RES = {
    implicit var s: Session = null;
    var tx: Transaction = null;
    try {
      s = getSession(sessionFactory)
      tx = s.beginTransaction()
      val result = func(s)
      tx.commit();
      result;
    } catch {
      case ex: Throwable => {
        try {
          if (tx != null) {
            tx.rollback();
          }
        } catch {
          case rollbakcException: Throwable => rollbakcException.printStackTrace();
        }
        throw new RuntimeException(ex)
      }
    } finally {
      if(s != null) {
        s.close();
      }
    }
  }

  private def getSession(sf: SessionFactory): Session = {
    try {
      sf.getCurrentSession()
    } catch {
      case t: Throwable => sf.openSession()
    }
  }

  def findById(id: PK): Option[T] = {
    inTransaction { implicit session =>
      dao.findById(id)
    }
  }

  def findAll(ordering: OrderBy*): Seq[T] = {
    inTransaction { implicit session =>
      dao.findAll(ordering: _*)
    }
  }
  
  // No-arg version, to avoid having to specify parentheses all the time when there is
  // no ordering applied 
  def findAll: Seq[T] = {
    inTransaction { implicit s =>  dao.findAll() }
  }

  def removeById(id: PK) = {
    inTransaction { implicit session =>
      dao.removeById(id)
    }
  }

  def remove(entity: T) = {
    inTransaction { implicit session =>
      dao.remove(entity)
    }
  }

  def save(entity: T): T = {
    inTransaction { implicit session =>
      dao.save(entity)
    }
  }

  def save(entities: Iterable[T]) = {
    inTransaction { implicit session =>
      dao.save(entities)
    }
  }

  def update(entity: T): T = {
    inTransaction { implicit session =>
      dao.update(entity)
    }
  }

  def update(entities: Iterable[T]): Unit = {
    inTransaction { implicit session =>
      dao.update(entities)
    }
  }
}
