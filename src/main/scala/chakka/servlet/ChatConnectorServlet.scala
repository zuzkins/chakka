package chakka.servlet

import org.eclipse.jetty.websocket.{WebSocket, WebSocketServlet}
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.websocket.WebSocket.Connection
import akka.actor.ActorSystem

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatConnectorServlet extends WebSocketServlet with ChatRoomNameParser {

  var actorSystem: ActorSystem = null

  def doWebSocketConnect(request: HttpServletRequest, protocol: String) = {

    val chatRoom = parseChatRoomName(Some(request.getPathInfo))

    chatRoom match {
      case Some(name) => joinChatRoom(name)
      case None       => leave
    }
  }

  private def joinChatRoom(name: String): WebSocket = {
    new WebSocket(){
      var conn: Option[Connection] = None

      def onOpen(connection: Connection) {
        conn = Option(connection)
      }

      def onClose(closeCode: Int, message: String) {
        for (c <- conn) c.close()

        conn = None
      }
    }
  }


  override def init() {
    this.actorSystem = ActorSystem("chakka")
  }

  private val leave: WebSocket = null
}

trait ChatRoomNameParser {

  def parseChatRoomName(urlPart: Option[String]): Option[String] = {
    for ( startOk   <- omitStarting_/(urlPart);
          name      <- roomName(startOk)) yield name
  }

  def omitStarting_/(path: Option[String]): Option[String] = {
    for (p <- path) yield {
      if (p.startsWith("/")) p substring 1
      else p
    }
  }

  def roomName(path: String): Option[String] = {
    val next_/ = path indexOf '/'
    val name = if (next_/ >= 0) path.substring(0, next_/) else path
    name match {
      case ""   => None
      case n    => Option(n)
    }
  }
}