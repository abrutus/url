package us.brut.brutify.models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.Query
import org.squeryl.KeyedEntity
import org.squeryl.PersistenceStatus
// Brutify
import us.brut.brutify.forms.ShortUrlForm

// Model class
case class ShortUrl(id: Long, short: String, url: String, hits: Int, ip : String, created : Int) extends KeyedEntity[Long] with PersistenceStatus {
  def this(url : String) = this(0, "", url, 0, "", 0)
  def this(url : String, short : String = "") = this(0, short, url, 0, "", 0)
}
// Auxiliary object
object ShortUrl {
  def create(shortUrlObj : ShortUrl) : Boolean = {
    inTransaction {
      val result = ShortUrlDb.shorturls.insert(shortUrlObj)
      if(result.isPersisted) {
        true
      } else {
        false
      }
    } 
  }
}

// Schema
object ShortUrlDb extends Schema {
  val shorturls = table[ShortUrl]("shorturls")
  
  on(shorturls)(shorturl => declare(
      shorturl.id is(autoIncremented)))
}
