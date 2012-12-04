package chakka.chat

import akka.actor.{ActorRef, ActorLogging, Actor}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
class ChatRoomActor(val name: String) extends ChatRoomManager with ChatController with ChatSocketFactory with ActorLogging with GsonProvider {


  override def receive = super[ChatRoomManager].receive orElse receiveChatMsgs

  def eventTarget = self
}

trait ChatRoomManager extends Actor { this: ChatSocketFactory with ActorLogging with JsonMessageReader =>
  var people = Vector[ChatSocket]()

  def name: String

  def receive = {
    case JoinRoom(_, username)      => sender ! acceptUser(username)
                                       self ! RefreshUsers
    case SocketActivated(ident)     => log.debug("Woohoo! " + ident + " is ready to rock in [" + name + "]")
    case SocketClosed(ident)        => removeSocket(ident)
                                       self ! RefreshUsers
    case MessageReceived(msg, id)   => onMessage(id, msg)
  }

  def removeSocket(ident: SocketIdent) {
    people = people.filterNot(_.ident == ident)
    log.debug("socket [" + ident + "] has been removed")
  }

  def acceptUser(username: String): ChatSocket = {
    val s = createSocketFor(username)
    s.registerLifecycleListener(eventTarget)
    people = people :+ s
    log.debug("user [" + username + "] connected")
    s
  }
}

trait ChatSocketFactory {

  def eventTarget: ActorRef

  def createSocketFor(username: String): ChatSocket = {
    val s = ChatSocket(username)
    s
  }
}