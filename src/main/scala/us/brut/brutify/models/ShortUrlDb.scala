package us.brut.brutify.models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.Query
import org.squeryl.KeyedEntity
import org.squeryl.PersistenceStatus
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
// Brutify
import us.brut.brutify.forms.ShortUrlForm
import us.brut.brutify.UrlGenerator

// Model class
case class ShortUrl(id: Long, short: String, url: String, hits: Int, ip: String, created: Int) extends KeyedEntity[Long] with PersistenceStatus {
  def this(url: String, short: String) = this(0, short, url, 0, "", 0)
  def this(url: String) = this(url, UrlGenerator.url())
  def this(url: String, short: String, collision: Boolean) = this(url, UrlGenerator.diff(short))

}
// Auxiliary object
object ShortUrl {
  def create(shortUrlObj: ShortUrl): ShortUrl = {
    try {
      inTransaction {
        val result = ShortUrlDb.shorturls.insert(shortUrlObj)
        result
      }
    } catch {
      case e: java.lang.RuntimeException =>
        e.getCause match {
          // If requesting or generated shorturl already taken
          case c: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException => return ShortUrl.create(new ShortUrl(shortUrlObj.url, shortUrlObj.short, true))
          case _ => throw e
        }
    }
  }
}

// Schema
object ShortUrlDb extends Schema {
  val shorturls = table[ShortUrl]("shorturls")
  def findByShort(short: String) = {
    shorturls.where(a => a.short === short).single
  }
  on(shorturls)(shorturl => declare(
    shorturl.id is (autoIncremented),
    shorturl.short is (unique,  dbType("varchar(750)"))))
}
