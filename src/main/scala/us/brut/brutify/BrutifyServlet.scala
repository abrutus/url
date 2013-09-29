package us.brut.brutify

import org.scalatra._
import scalate.ScalateSupport
// json libs
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class BrutifyServlet extends BrutifyStack with JacksonJsonSupport {
      protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  get("/") {
    ssp("/index", "title" -> "brut.us")
  }
  post("/create") {
    contentType = formats("json")
    //(theCode: Int, theMessage : String, theURLObj : ShortenedURL)
    JsonResponse(200, "OK", ShortenedURL(5,"ab","http://test.com",0, "127.0.0.1",0))
  }
}
