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
import scala.util.Try

// Controller
class ShortUrlController extends BrutifyStack with JacksonJsonSupport with DBSessionSupport {
  val DOMAIN = "brut.us"
  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  val logger = LoggerFactory.getLogger(getClass)
  // Handle short urls
  get("/:shorturl") {
    val dbObj = Try(ShortUrlDb.findByShort(params("shorturl")))
    val urlObj = dbObj.getOrElse(new ShortUrl("", "404"))
    halt(status = 301, headers = Map("Location" -> urlObj.url))
  }
  // Handle (shorturl).jpg automatically
  get("""^\/(.*)\.jpg""".r) {
    val shortUrl = multiParams("captures").head
    redirect("/" + shortUrl)
  }
  get("/qr/:shorturl") {
    val dbObj = Try(ShortUrlDb.findByShort(params("shorturl")))
    val urlObj = dbObj.getOrElse(new ShortUrl("", "404"))
    if(urlObj.short == "404") {
      halt(status = 301, headers = Map("Location" -> urlObj.url))
    }
    val url : String = "http://" + DOMAIN + "/" + urlObj.short
    ssp("/qr", "url" -> url,  "title" -> "brut.us")
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
      // simple check for valid args
      if (shortUrlReq.url.length < 6 || shortUrlReq.url.startsWith("http://" + DOMAIN) || shortUrlReq.url.startsWith("http://www." + DOMAIN)) {
        throw new java.lang.IllegalArgumentException("Url is short already")
      }
      if(!shortUrlReq.url.contains("://"))
        throw new java.lang.IllegalArgumentException("If you'd enter a valid URL that'd be great")
      // Turn into db model class
      val urlObj = if (shortUrlReq.short.isEmpty())
        new ShortUrl(request.remoteAddress, shortUrlReq.url)
      else
        new ShortUrl(request.remoteAddress, shortUrlReq.url, shortUrlReq.short);
      // Persist, may throw plethora(5) of db Exceptions.
      val dbPersist = ShortUrl.create(urlObj)
      if (dbPersist.isPersisted)
        JsonResponse(200, "OK", Some(dbPersist))
      else
        JsonResponse(500, "Could not persist to the database", None)
    } catch {
      case ex: org.json4s.package$MappingException => JsonResponse(500, "Error parsing JSON payload", None)
      case ex: java.lang.IllegalArgumentException => JsonResponse(500, ex.getMessage, None)
    }
  }
  // Setup DB
  get("/create-db") {
    contentType = "text/html"
    // Only accessible from localhost IPv4 and IPv6
    if (!List("127.0.0.1", "0:0:0:0:0:0:0:1").contains(request.remoteAddress))
      halt(403, "Forbbiden" + request.remoteAddress)
    ShortUrlDb.create
    redirect("/")
  }
  get("/404") {
    halt(404, <h1>Not Found! <br/><img src="/_static/404.gif"/></h1>)
  }
  // Homepage
  get("/") {
    ssp("/index", "title" -> "brut.us")
  }
}