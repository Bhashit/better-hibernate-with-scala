package setup.modules

import java.util.Properties
import org.hibernate.EntityMode
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.{StandardServiceRegistryBuilder, BootstrapServiceRegistryBuilder}
import org.hibernate.cfg.AvailableSettings
import org.hibernate.boot.MetadataSources
import models._
import persistence.property.CustomPojoEntityTuplizer
import javax.inject._
import play.api.inject._
import play.api._
import com.google.inject.{Provides, AbstractModule}
import play.api.db._


class PersistenceModule extends AbstractModule {

  def configure = {}

  @Provides @Singleton
  private def buildSessionFactory(): SessionFactory = {
    import play.api.Play.current
    _root_.persistence.property.ScalaPropertyAccessor.playApplication = Option(Play.current)

    val srb = new StandardServiceRegistryBuilder()
      .applySettings(getHibernateProperties)
      .applySetting(AvailableSettings.DATASOURCE, DB.getDataSource())

    val standardRegistry = srb.build()
    val sources = new MetadataSources(standardRegistry)
    addAnnotatedClasses(sources);
    val metadataBuilder = sources.getMetadataBuilder()
    val metadata = metadataBuilder.build();
    val sfBuilder = metadata.getSessionFactoryBuilder()
    // Our custom Tuplizer to take care of custom property access.
    sfBuilder.applyEntityTuplizer(EntityMode.POJO, classOf[CustomPojoEntityTuplizer])

    sfBuilder.build()
  }

  private def getHibernateProperties = {
    val properties = new Properties()
    val is = Play.current.resourceAsStream("hibernate.properties").get
    properties.load(is)
    is.close()
    properties
  }

  private def addAnnotatedClasses(implicit configuration: MetadataSources) = {
    configuration.addAnnotatedClass(classOf[User])
    configuration.addAnnotatedClass(classOf[UserProfile])
    configuration.addAnnotatedClass(classOf[OAuth1])
    configuration.addAnnotatedClass(classOf[OAuth2])
    configuration.addAnnotatedClass(classOf[models.Role])
    configuration.addAnnotatedClass(classOf[models.values.Country]) 
  }
}
