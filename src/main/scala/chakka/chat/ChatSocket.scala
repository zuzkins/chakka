package chakka.chat

import org.eclipse.jetty.websocket.WebSocket
import org.eclipse.jetty.websocket.WebSocket.{OnTextMessage, Connection}
import akka.actor.ActorRef
import java.util.UUID

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

case class ChatSocket(ident: SocketIdent) extends OnTextMessage{

  private var conn: Option[Connection] = None

  private var onSocketActivated: Option[ActorRef] = None
  private var onClosedListener: Option[ActorRef] = None
  private var onMessageListener: Option[ActorRef] = None

  def registerLifecycleListener(ref: ActorRef) {
    val it = Option(ref)
    onSocketActivated = it
    onClosedListener = it
    onMessageListener  = it
  }

  def onOpen(connection: Connection) {
    conn = Option(connection)
    for (c <- conn; if c.isOpen; list <- onSocketActivated) {
      list ! SocketActivated(ident)
      onSocketActivated = None
    }
  }

  def onClose(closeCode: Int, message: String) {
    try {
      for (c <- conn; if c.isOpen) c.close(closeCode, message)
    } catch {
      case e: Exception => println("Connection couldn't be closed:" + e.getMessage)
    }

    for (list <- onClosedListener) {
      list ! SocketClosed(ident)
      onClosedListener = None
    }
  }

  def onMessage(data: String) {
    for (list <- onMessageListener) list ! MessageReceived(data, ident)
  }
}

object ChatSocket {
  def apply(username: String): ChatSocket = ChatSocket(SocketIdent(username))
}

case class SocketIdent(username: String, id: UUID = UUID.randomUUID())