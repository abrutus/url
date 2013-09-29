package us.brut.brutify

import org.scalatra._
import scalate.ScalateSupport

class BrutifyServlet extends BrutifyStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
