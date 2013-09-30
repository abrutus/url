package us.brut.brutify
// Original
import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.json._
import org.slf4j.{ Logger, LoggerFactory }
// Persistance
import us.brut.brutify.data.DBSessionSupport
// Additions
import us.brut.brutify.forms._
import us.brut.brutify.models._

// Controller
class BrutifyServlet extends BrutifyStack with JacksonJsonSupport with DBSessionSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  val logger = LoggerFactory.getLogger(getClass)
  // Homepage
  get("/") {
    ssp("/index", "title" -> "brut.us")
  }
  // Homepage that lets you specify which shorturl
  get("/custom") {
    ssp("/custom", "title" -> "brut.us")
  }
  // JSON endpoint for creating short urls
  post("/create") {
    contentType = formats("json")
    // Parse Incoming JSON as a ShortenedURLPost 
    try {
      // may throw package$MappingException
      val shortUrlReq: ShortUrlForm = parsedBody.extract[ShortUrlForm]
      // Turn into db model class
      val urlObj = new ShortUrl(shortUrlReq.url, shortUrlReq.short)
      // Persist, may throw plethora of db Exceptions.
      if (ShortUrl.create(urlObj))
        JsonResponse(200, "OK", Some(urlObj))
        else
      JsonResponse(500, "Could not persist to the database", None)
    } catch {
      case ex: org.json4s.package$MappingException => JsonResponse(500, "Error parsing JSON payload", None)
    }
  }

  get("/create-db") {
    contentType = "text/html"

    ShortUrlDb.create
    redirect("/")
  }
}

    