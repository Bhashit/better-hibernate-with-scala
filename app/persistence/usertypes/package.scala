package persistence.usertypes

import org.hibernate.`type`.StandardBasicTypes._

class OptionInt extends AbstractOptionType(classOf[Int])

class OptionLong extends AbstractOptionType(classOf[Long])

class OptionFloat extends AbstractOptionType(classOf[Float])

class OptionDouble extends AbstractOptionType(classOf[Double])

class OptionBoolean extends AbstractOptionType(classOf[Boolean])

