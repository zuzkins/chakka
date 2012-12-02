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
  case class ChatMessage(msg: String)
  case class ChatMessageCommand(body: ChatMessage) extends IsCommand {
    val name = ChatMessageCommand.name
  }

  object ChatMessageCommand {
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

  case class BroadCastCommand(override val command: IsCommand) extends CommandWrapper(command) {
    def filterRecipients(all: Seq[ChatSocket]) = all
  }

  case class BroadCastToOthersCommand(sender: SocketIdent, override val command: IsCommand) extends CommandWrapper(command) {
    def filterRecipients(all: Seq[ChatSocket]) = all.filterNot(_.ident == sender)
  }

  case class PrivateCommand(recipients: Set[String], override val command: IsCommand) extends CommandWrapper(command) {
    def filterRecipients(all: Seq[ChatSocket]) = all filter(recipients contains _.ident.username)
  }
  object PrivateCommand {
    def apply(username: String, cmd: IsCommand): PrivateCommand = PrivateCommand(Set(username), cmd)
  }
}
