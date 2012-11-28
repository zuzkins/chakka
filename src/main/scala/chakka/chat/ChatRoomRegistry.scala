package chakka.chat

import akka.actor.{ActorLogging, Actor}
import chakka.model.ChatRoom

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomRegistry extends Actor with ActorLogging {

  var chatRooms = Set.empty[ChatRoom]

  def receive = {
    case ListRooms => listRooms()
  }

  def listRooms() {
    sender ! chatRooms.toList
  }
}