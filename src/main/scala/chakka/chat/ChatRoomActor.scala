package chakka.chat

import akka.actor.{ActorRef, ActorLogging, Actor}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
class ChatRoomActor extends ChatRoomManager with ChatController with ChatSocketFactory with ActorLogging with GsonProvider {


  override def receive = super[ChatRoomManager].receive orElse receiveChatMsgs

  def eventTarget = self
}

trait ChatRoomManager extends Actor { this: ChatSocketFactory with ActorLogging =>
  var people = Vector[ChatSocket]()

  def receive = {
    case JoinRoom(_, username)      => sender ! acceptUser(username)
    case SocketActivated(ident)     => log.debug("Woohoo! " + ident + " is ready to rock")
    case SocketClosed(ident)        => removeSocket(ident)
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