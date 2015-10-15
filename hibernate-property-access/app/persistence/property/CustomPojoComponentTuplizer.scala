package persistence.property

import org.hibernate.mapping.Property
import org.hibernate.property.access.spi.{Getter, Setter}
import org.hibernate.tuple.component.PojoComponentTuplizer
import org.hibernate.mapping.Component

// See the comments on CustomPojoEntityTuplizer.
// Waiting for https://hibernate.atlassian.net/browse/HHH-1907 and a few others.
// In general, all the necessary knobs should be available by the time
// Hibernate 5 comes out. Ideally, using this class shouldn't be required.
// However, if you want to use a different kind of a strategry for tuplizing
// individual components, you can use this thing with the @Tuplizer annotation
// for @Embeddable classes. For Entity classes, CustomPojoEntityTuplizer would
// be used.
class CustomPojoComponentTuplizer(component: Component) extends PojoComponentTuplizer(component) {
  override def buildGetter(component: Component, prop: Property): Getter = {
    prop.setPropertyAccessorName("persistence.property.ScalaPropertyAccessStrategy")
    prop.getGetter(component.getComponentClass())
  }

  override def buildSetter(component: Component, prop: Property): Setter = {
    prop.setPropertyAccessorName("persistence.property.ScalaPropertyAccessStrategy")
    prop.getSetter(component.getComponentClass())
  }
}
