package setup.modules

import java.util.Properties
import javax.persistence.AttributeConverter
import org.hibernate.EntityMode
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import play.Play
import scaldi.Module
import models._
import persistence.property.CustomPojoEntityTuplizer

class PersistenceModule extends Module {

  val sessionFactory = buildSessionFactory()

  bind[SessionFactory] to sessionFactory

  private def buildSessionFactory() = {
    val configuration = new Configuration

    // Our custom Tuplizer to take care of custom property access.
    configuration.getEntityTuplizerFactory.
      registerDefaultTuplizerClass(EntityMode.POJO, classOf[CustomPojoEntityTuplizer])

    configuration.setProperties(getHibernateProperties)

    addAnnotatedClasses(configuration)
    val srb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties);
    configuration.buildSessionFactory(srb.build());
  }

  private def getHibernateProperties = {
    val properties = new Properties()
    val is = Play.application().resourceAsStream("hibernate.properties")
    properties.load(is)
    is.close()
    properties
  }

  private def addAnnotatedClasses(configuration: Configuration) = {
    configuration.addAnnotatedClass(classOf[User])
    configuration.addAnnotatedClass(classOf[UserProfile])
    configuration.addAnnotatedClass(classOf[OAuth1])
    configuration.addAnnotatedClass(classOf[OAuth2])
    configuration.addAnnotatedClass(classOf[models.Role])
    configuration.addAnnotatedClass(classOf[models.Country])
  }
}
