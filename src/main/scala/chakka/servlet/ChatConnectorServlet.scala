package chakka.servlet

import org.eclipse.jetty.websocket.{WebSocket, WebSocketServlet}
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.websocket.WebSocket.Connection

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatConnectorServlet extends WebSocketServlet {

  def doWebSocketConnect(request: HttpServletRequest, protocol: String) = {
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
}