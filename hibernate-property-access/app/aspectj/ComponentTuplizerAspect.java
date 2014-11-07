package aspectj;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.hibernate.EntityMode;
import org.hibernate.tuple.component.ComponentTuplizer;

import persistence.property.CustomPojoComponentTuplizer;

import org.slf4j.*;

@Aspect
public class ComponentTuplizerAspect {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Around("execution (private static * org.hibernate.tuple.component.ComponentTuplizerFactory.buildBaseMapping())")
  public Object setupDefaultComponentTuplizer(ProceedingJoinPoint pjp) throws Throwable  {
    logger.debug("Applying aspect around ComponentTuplizerFactory.buildBaseMapping()");
    java.util.Map<EntityMode, Class<? extends ComponentTuplizer>> map
      = (java.util.Map<EntityMode, Class<? extends ComponentTuplizer>>) pjp.proceed();
    map.put(EntityMode.POJO, CustomPojoComponentTuplizer.class);
    return map;
  }
}
