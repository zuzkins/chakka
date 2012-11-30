package chakka.chat

import akka.actor.Actor

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomManager extends Actor {

  var people = Set.empty[ChatSocket]

  def receive = {
    case JoinRoom(username, _) => sender ! acceptUser(username)
  }

  def acceptUser(username: String): ChatSocket = {
    val s = ChatSocket(username)
    people += s

    s
  }
}