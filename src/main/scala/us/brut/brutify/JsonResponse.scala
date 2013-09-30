package us.brut.brutify
import us.brut.brutify.models._
case class JsonResponse(code: Int, message : String, urlObj : Option[ShortUrl])