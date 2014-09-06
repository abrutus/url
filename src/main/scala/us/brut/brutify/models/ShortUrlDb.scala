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
case class ShortUrl(id: Long, short: String, url: String, var hits: Int, ip: String, created: Long) extends KeyedEntity[Long] with PersistenceStatus {
  def this(ip: String, url: String, short: String) = this(0, short, url, 0, ip,  System.currentTimeMillis / 1000)
  def this(ip: String, url: String) = this(ip, url, UrlGenerator.url())
  def this(ip: String, url: String, short: String, collision: Boolean) = this(ip, url, UrlGenerator.diff(short))

}
// Auxiliary object
object ShortUrl {
  def update(shortUrlObj: ShortUrl): ShortUrl = {
    try {
      inTransaction {
        val result = ShortUrlDb.shorturls.update(shortUrlObj)
        shortUrlObj
      }
    } catch {
      case e: java.lang.RuntimeException =>
        e.getCause match {
          // If requesting or generated shorturl already taken
          case c: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException => return ShortUrl.create(new ShortUrl(shortUrlObj.ip, shortUrlObj.url, shortUrlObj.short, true))
          case _ => throw e
        }
    }
  }
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
          case c: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException => return ShortUrl.create(new ShortUrl(shortUrlObj.ip, shortUrlObj.url, shortUrlObj.short, true))
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
    shorturl.short is (unique,  dbType("varchar(32)")),
    shorturl.url is (dbType("varchar(4048)")
    )))
}
