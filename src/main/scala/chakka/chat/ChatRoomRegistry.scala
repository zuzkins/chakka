package chakka.chat

import akka.actor.{Props, ActorLogging, Actor}
import chakka.model.ChatRoom
import akka.pattern.pipe

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomRegistry extends Actor with ActorLogging {

  var chatRooms = Set.empty[ChatRoom]

  def receive = {
    case ListRooms                            => listRooms()
    case msg: JoinRoom                        => joinRoom(msg)
  }

  def listRooms() {
    sender ! chatRooms.toList
  }

  def joinRoom(msg: JoinRoom) {
    val roomName = msg.roomName
    val room = chatRooms.find(_.name == roomName) match {
      case Some(r)    => r
      case None       =>
        val ref = context.system.actorOf(Props(new ChatRoomActor(msg.roomName)))
        val newRoom = ChatRoom(roomName, ref)
        chatRooms += newRoom
        newRoom
    }

    room.actor forward msg
  }
}