package persistence.property

import scala.reflect.runtime.{universe => ru}
import scala.reflect.api._

import java.beans.Introspector
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.Map
import _root_.org.hibernate.{PropertyAccessException, PropertyNotFoundException, PropertySetterAccessException}
import _root_.org.hibernate.engine.spi.{SessionFactoryImplementor, SessionImplementor}
import _root_.org.hibernate.property.access.spi.{Getter, Setter, PropertyAccess, PropertyAccessStrategy}
import _root_.org.hibernate.property.access.internal.PropertyAccessBasicImpl
import _root_.org.slf4j.LoggerFactory

class ScalaPropertyAccessStrategy extends PropertyAccessStrategy {

    override def buildPropertyAccess(containerJavaType: Class[_], propertyName: String) = {
      new ScalaPropertyAccessor(this, containerJavaType, propertyName );
    }
}

object ScalaPropertyAccessStrategy {
  val INSTANCE = new ScalaPropertyAccessStrategy();
}

// TODO this class might need some work to make it look better. Keep an eye on it.
class ScalaPropertyAccessor(val strategy: ScalaPropertyAccessStrategy, 
			val containerJavaType: Class[_],
			val propertyName: String) extends PropertyAccess {

  import ScalaPropertyAccessor._

  private lazy val setter = createSetter(containerJavaType, propertyName)
  private lazy val getter = createGetter(containerJavaType, propertyName)

  override def getSetter(): Setter = setter

  override def getGetter(): Getter = getter

  override def getPropertyAccessStrategy() = strategy

}


object ScalaPropertyAccessor {

  var playApplication: Option[play.api.Application] = None

  // In scala, the setter for the vars is named like <property-name>_$eq
  private def setterMethod(theClass: Class[_], propertyName: String) = {
    val getter = getGetterOrNull(theClass, propertyName)
    val returnType = if (getter == null) null else getter.getReturnType
    val methods = theClass.getDeclaredMethods()
    // TODO add check for the argument type. Also, once the scala reflection API is
    // table enough, move all code to use it.
    methods.
      filter(_.getParameterTypes.length == 1).
      find(_.getName == (propertyName + "_$eq")).getOrElse(null)
  }

  private final class BasicGetter(private val clazz: Class[_], private val method: Method, private val propertyName: String) extends Getter {
    override def get(target: AnyRef): java.lang.Object = {
      val returned = method.invoke(target)
      if(returned.isInstanceOf[Option[_]]) {
        returned.asInstanceOf[Option[_]].getOrElse(null).asInstanceOf[Object]
      } else {
        returned
      }
    }

    override def getForInsert(target: AnyRef,
        mergeMap: java.util.Map[_, _],
        session: SessionImplementor) = {
      get(target)
    }

    override val getReturnType: Class[_] = {
      val returnType = getReturnTypeForGetter(clazz, propertyName)
      val className = if(isReturnTypeOption(returnType)) {
        returnType.typeArgs(0).typeSymbol.fullName
      } else {
        returnType.typeSymbol.fullName
      }

      if(playApplication.isDefined) {
        playApplication.get.classloader.loadClass(className)
      } else {
        throw new IllegalStateException("The Play Application ClassLoader must be set before building the SessionFactory")
      }
    }

    override def getMember() = method

    // This is an optional method in the Getter interface. The documented approach is to
    // return null when you don't want to implement this method. Keeping it unimplemented
    // so that the method isn't exposed and the getting/setting is always intercepted by
    // us.
    override def getMethod() = null

    override def getMethodName() = method.getName

    override def toString() = "BasicGetter(" + clazz.getName() + '.' + propertyName + ')'

    def readResolve(): AnyRef = createGetter(clazz, propertyName)
  }

  private def isReturnTypeOption(returnType: ru.Type) = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val optionTypeSymbol = mirror.classSymbol(classOf[Option[_]])
    returnType.typeSymbol == optionTypeSymbol
  }

  private def getReturnTypeForGetter(clazz: Class[_], propertyName: String): ru.Type = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val classSymbol = mirror.classSymbol(clazz)
    val getter = classSymbol.selfType.member(ru.TermName(propertyName)).asMethod.getter
    getter.info.resultType
  }

  private final class BasicSetter(val clazz: Class[_], val method: Method, val propertyName: String, val isOptionType: Boolean) extends Setter {
    override def set(target: AnyRef, value: AnyRef, factory: SessionFactoryImplementor) = {
      val valueToSet = if(isOptionType) Option(value) else value
      method.invoke(target, valueToSet)
    }

    // see the comment on BasicGetter#getMethod
    override def getMethod() = null

    override def getMethodName() = method.getName

    def readResolve() = createSetter(clazz, propertyName)

    override def toString() = "BasicSetter(" + clazz.getName() + '.' + propertyName + ')'
  }

  private def createSetter(theClass: Class[_], propertyName: String): Setter = {
    val result: BasicSetter = getSetterOrNull(theClass, propertyName);
    if (result == null) {
      throw new PropertyNotFoundException(
        "Could not find a setter for property " +
          propertyName +
          " in class " +
          theClass.getName()
      )
    }
    return result
  }

  private def getSetterOrNull(theClass: Class[_], propertyName: String): BasicSetter = {
    if (theClass == classOf[Object] || theClass == null) null
    else {
      val method = setterMethod(theClass, propertyName)
      if (method != null) {
        method.setAccessible(true)
        val returnType = getReturnTypeForGetter(theClass, propertyName)
        new BasicSetter(theClass, method, propertyName, isReturnTypeOption(returnType))
      } else {
        var setter = getSetterOrNull(theClass.getSuperclass(), propertyName)
        if (setter == null) {
          val interfaces = theClass.getInterfaces()
          var i = 0
          while (i < interfaces.length && setter == null) {
            setter = getSetterOrNull(interfaces(i), propertyName)
            i += 1
          }

        }
        setter
      }
    }
  }


  private def createGetter(theClass: Class[_], propertyName: String): BasicGetter = {
    val result = getGetterOrNull(theClass, propertyName);
    if (result == null) {
      throw new PropertyNotFoundException(s"Could not find a getter for $propertyName in class ${theClass.getName()}")
    } else {
      result
    }
  }

  private def getGetterOrNull(theClass: Class[_], propertyName: String): BasicGetter = {
    if (theClass == classOf[Object] || theClass == null) {
      return null;
    }

    val method = getterMethod(theClass, propertyName);

    if (method != null) {
      method.setAccessible(true);
      return new BasicGetter(theClass, method, propertyName);
    } else {
      var getter = getGetterOrNull(theClass.getSuperclass(), propertyName);
      if (getter == null) {
        val interfaces = theClass.getInterfaces();
        var i = 0
        while (i < interfaces.length && getter == null) {
          getter = getGetterOrNull(interfaces(i), propertyName);
          i += 1
        }
      }
      getter;
    }
  }

  private def getterMethod(theClass: Class[_], propertyName: String): Method = {
    val methods = theClass.getDeclaredMethods()
    methods.
      filter(_.getParameterTypes().length == 0).
      filter(!_.isBridge).
      find(_.getName == propertyName).getOrElse(null)
  }
}
