package persistence.property;

import org.hibernate.mapping.*;
import org.hibernate.tuple.entity.*;
import org.hibernate.metamodel.binding.*;
import org.hibernate.property.*;


// Need to write this in java since both constructors need to be present
// in this class. It is required by the EntityTuplizerFactory. Scala
// doesn't allow calling multiple super-class constructors.
//
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
public class CustomPojoEntityTuplizer extends PojoEntityTuplizer {
  public CustomPojoEntityTuplizer(EntityMetamodel emm, EntityBinding eb) {
    super(emm, eb);
  }

  public CustomPojoEntityTuplizer(EntityMetamodel emm, PersistentClass pc) {
    super(emm, pc);
  }

  @Override
  protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
    mappedProperty.setPropertyAccessorName("persistence.property.ScalaPropertyAccessor");
    return super.buildPropertyGetter(mappedProperty, mappedEntity);
  }

  @Override
  protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
    mappedProperty.setPropertyAccessorName("persistence.property.ScalaPropertyAccessor");
    return super.buildPropertySetter(mappedProperty, mappedEntity);
  }

  // The following two only get called for id property if it is 'virtual', whatever
  // the hell that is. If we run into problems, check to see if this could have
  // something to do with it.
  @Override
  protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
    return super.buildPropertyGetter(mappedProperty);
  }

  @Override
  protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
    return super.buildPropertySetter(mappedProperty);
  }

}
