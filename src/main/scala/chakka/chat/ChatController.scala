package chakka.chat

import akka.actor.{ActorLogging, Actor}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
trait ChatController extends CommandProcessor with CommandSender with JsonMessageReader { this: Actor with ActorLogging =>

  val receiveChatMsgs: PartialFunction[Any, Unit] = sendWebSocketMessages orElse {
    case RefreshUsers               => refreshUserLists()
  }

  def onCommand(cmd: CommandFromWebSocket) {

    def matchMessage: PartialFunction[Any, Unit] = {
      case msg: Message               => self ! BroadCastCommand(ChatMessageCommand(msg.copy(sender = cmd.sender.username)))
      case ListUsersCommand.name      => listUsers(cmd.sender)
    }

    def logMessage: PartialFunction[Any, Unit] = {
      case x   => onUnknownCommand(cmd.sender, "Command [" + x.getClass.getSimpleName +"] is uknown")
    }

    val matcher = matchMessage orElse logMessage

    matcher.apply(cmd.cmd)
  }

  def collectActiveUsernames: Vector[String] = {
    people.map(_.ident.username).sortWith((n1, n2) => n1.compareTo(n2) < 0)
  }


  def listUsers(ident: SocketIdent) {
    val names = collectActiveUsernames

    self ! PrivateCommand(ident.username, UserListCommand(UserList(names)))
  }

  def refreshUserLists() {
    val names = collectActiveUsernames
    self ! BroadCastCommand(UserListCommand(UserList(names)))
  }

  def onUnknownCommand(sender: SocketIdent, content: String) {
    self ! PrivateCommand(sender.username, UnknownCommand(Message(content)))
  }


  def onEmptyMessage(sender: SocketIdent) {
    localLog.info("Empty message from [" + sender + "]")
  }

  def onInvalidCommand(sender: SocketIdent, msg: String) {
    localLog.info("Invalid command from [" + sender + "]: " + msg)
  }

  val cmdMapping = Map(ChatMessageCommand.name -> classOf[Message], ListUsersCommand.name -> NoBodyCommand.getClass)
  def bodyType(commandType: String) = cmdMapping.get(commandType)
}