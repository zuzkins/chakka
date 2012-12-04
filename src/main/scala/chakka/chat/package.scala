package chakka

import model.ChatRoom
import collection.JavaConverters._

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
package object chat {

  object ListRooms
  object RefreshUsers
  case class JoinRoom(roomName: String, username: String)
  case class Rooms(rooms: List[ChatRoom])

  case class MessageReceived(msg: String, ident: SocketIdent)
  case class SocketActivated(ident: SocketIdent)
  case class SocketClosed(ident: SocketIdent)


  /** COMMANDS **/
  case class Message(msg: String, sender: String = null)
  case class ChatMessageCommand(body: Message) extends IsCommand {
    val name = ChatMessageCommand.name
  }

  object ChatMessageCommand {
    val name = "msg"
  }

  object ListUsersCommand extends IsCommand {
    val name = "listUsers"
    val body = null
  }

  case class UserListCommand(body: UserList) extends IsCommand {
    val name = "userList"
  }

  case class UserList(usernames: java.util.List[String])

  object UserList {

    def apply(usernames: Seq[String]): UserList = UserList(usernames.asJava)
  }

  case class UnknownCommand(body: Message) extends IsCommand {
    val name = UnknownCommand.name
  }

  object UnknownCommand {
    val name = "unknownCommand"
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
