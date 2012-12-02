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


  /** COMMANDS **/
  case class ChatMsg(msg: String)
  case class ChatMessageCommand(body: ChatMsg) extends IsCommand {
    val name = "msg"
  }

  case class CommandFromWebSocket(sender: SocketIdent, cmd: AnyRef)
  object NoBodyCommand

  trait IsCommand {
    def name: String
    def body: AnyRef
  }

  abstract class CommandWrapper(val command: IsCommand) {

    def filterRecipients(all: Seq[ChatSocket]): Seq[ChatSocket]
  }
}
