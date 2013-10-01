import us.brut.brutify._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with DBInit {
  override def init(context: ServletContext) {
    configureDb()
    context.mount(new ShortUrlController, "/*")
  }
  override def destroy(context:ServletContext) {
    closeDbConnection()
  }
}
