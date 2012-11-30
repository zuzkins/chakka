package chakka.chat

import org.eclipse.jetty.websocket.WebSocket
import org.eclipse.jetty.websocket.WebSocket.Connection
import akka.actor.ActorRef

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

case class ChatSocket(username: String) extends WebSocket {

  private var conn: Option[Connection] = None

  def onOpen(connection: Connection) {
    conn = Option(connection)
  }

  def onClose(closeCode: Int, message: String) {
    try {
      for (c <- conn; if c.isOpen) c.close(closeCode, message)
    } catch {
      case e: Exception => println("Connection couldn't be closed:" + e.getMessage)
    }
  }
}