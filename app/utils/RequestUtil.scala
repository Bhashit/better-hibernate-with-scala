package utils

object RequestUtil {
  def toJsonString(value: AnyRef) = {
    import org.json4s.jackson.Serialization
    import org.json4s.NoTypeHints
    implicit val formats = Serialization.formats(NoTypeHints)
    Serialization.write(value)
  }
}