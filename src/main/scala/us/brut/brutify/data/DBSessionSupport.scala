package us.brut.brutify.data

import org.squeryl.Session
import org.squeryl.SessionFactory
import org.scalatra._

object DBSessionSupport {
  val key = {
    val n = getClass.getName
    if (n.endsWith("$")) n.dropRight(1) else n
  }
}

trait DBSessionSupport { this: ScalatraBase =>
  import DBSessionSupport._

  def dbSession = request.get(key).orNull.asInstanceOf[Session]

  before() { 
    request(key) = SessionFactory.newSession 
    dbSession.bindToCurrentThread 
  }

  after() {
    dbSession.close
    dbSession.unbindFromCurrentThread
  }

}