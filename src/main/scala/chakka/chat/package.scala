package chakka

import model.ChatRoom

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
package object chat {

  object ListRooms
  case class JoinRoom(roomName: String, username: String)
  case class Rooms(rooms: List[ChatRoom])

  case class MessageReceived(msg: String, ident: SocketIdent)
  case class SocketActivated(ident: SocketIdent)
  case class SocketClosed(ident: SocketIdent)
}
