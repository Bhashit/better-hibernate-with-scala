package persistence.property;

import org.hibernate.mapping._
import org.hibernate.tuple.entity._
import org.hibernate.property.access.spi.{Getter, Setter}

// The way this class overrides the property-accessors is supremely
// hacky. Keep an eye on the relevant issue at:
// https://hibernate.atlassian.net/browse/HCANN-48. If that issue is
// resolved, we can remove this class.
//
// Also, in here we are assuming that all property access will be through the
// scala getter/setters. So, this doesn't allow for java-bean style
// getters and setters. If we need to allow that, modify the code to read
// appropriate Access annotations and behave accordingly.
//
// Also, we still need to put the org.hibernate.annotations.Type
// annotations everywhere the Option types are used. Try to find a way to
// do away with that as well.
class CustomPojoEntityTuplizer(emm: EntityMetamodel, pc: PersistentClass) extends PojoEntityTuplizer(emm, pc) {

  override def buildPropertyGetter(mappedProperty: Property, mappedEntity: PersistentClass) = {
    mappedProperty.setPropertyAccessorName("persistence.property.ScalaPropertyAccessStrategy");
    super.buildPropertyGetter(mappedProperty, mappedEntity);
  }

  override def buildPropertySetter(mappedProperty: Property, mappedEntity: PersistentClass) = {
    mappedProperty.setPropertyAccessorName("persistence.property.ScalaPropertyAccessStrategy");
    super.buildPropertySetter(mappedProperty, mappedEntity);
  }
}
