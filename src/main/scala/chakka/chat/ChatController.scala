package chakka.chat

import akka.actor.{ActorLogging, Actor}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
trait ChatController extends CommandProcessor with CommandSender with JsonMessageReader { this: Actor with ActorLogging =>

  val chatController: PartialFunction[Any, Unit] = sendWebSocketMessages

  def onCommand(cmd: CommandFromWebSocket) {
    def matchMessage: PartialFunction[Any, Unit] = {
      case msg: Message   => self ! BroadCastCommand(ChatMessageCommand(msg))
    }

    def logMessage: PartialFunction[Any, Unit] = {
      case x   => onUnknownCommand(cmd.sender, "Command [" + x.getClass.getSimpleName +"] is uknown")
    }

    val matcher = matchMessage orElse logMessage

    matcher.apply(cmd.cmd)
  }


  def onUnknownCommand(sender: SocketIdent, content: String) {
    self ! PrivateCommand(sender.username, UnknownCommand(Message(content)))
  }


  def onEmptyMessage(sender: SocketIdent) {
    log.info("Empty message from [" + sender + "]")
  }

  def onInvalidCommand(sender: SocketIdent, msg: String) {
    log.info("Invalid command from [" + sender + "]: " + msg)
  }

  val cmdMapping = Map(ChatMessageCommand.name -> classOf[Message])
  def bodyType(commandType: String) = cmdMapping.get(commandType)
}