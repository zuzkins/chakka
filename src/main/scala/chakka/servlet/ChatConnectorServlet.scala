package chakka.servlet

import org.eclipse.jetty.websocket.{WebSocket, WebSocketServlet}
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.websocket.WebSocket.Connection
import akka.actor.{Props, ActorRef, ActorSystem}
import java.io.{InputStreamReader, BufferedInputStream, BufferedReader}

import concurrent.duration._
import akka.util.Timeout
import chakka.chat.{ChatSocket, ChatRoomRegistry, JoinRoom}
import akka.pattern.ask
import concurrent.Await
import org.slf4j.LoggerFactory
import javax.servlet.{ServletResponse, ServletRequest}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatConnectorServlet extends WebSocketServlet with ChatRoomNameParser {

  val log = LoggerFactory.getLogger(this.getClass)

  implicit val timeout = Timeout(300 millis)

  var actorSystem: ActorSystem = null
  var registryActor: ActorRef = null
  var CHAT_REGISTRY_NAME = "chatRegistry"


  override def service(req: ServletRequest, res: ServletResponse) {
    super.service(req, res)
  }

  def doWebSocketConnect(request: HttpServletRequest, protocol: String) = {

    val chatRoom = parseChatRoomName(Some(request.getPathInfo))

    chatRoom match {
      case Some(roomIdent) => joinChatRoom(roomIdent)
      case None       => leave
    }
  }

  private def joinChatRoom(roomIdent: String): WebSocket = {
    val parts = roomIdent.split("!")
    val name = parts(0)
    val username = parts(1)
    val msg = JoinRoom(name, username)

    val future = ask(registryActor, msg).mapTo[ChatSocket]

    Await.result(future, timeout.duration)
  }


  override def init() {
    super.init()

    log.info("Starting!")
    this.actorSystem = ActorSystem("chakka")

    try {
      registryActor = actorSystem.actorOf(Props[ChatRoomRegistry], CHAT_REGISTRY_NAME)
    } catch {
      case e: Throwable => log.warn("Error while creating registry actor: ", e)
    }
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