package chakka.chat

import akka.actor.Actor

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
trait ChatController extends CommandProcessor with CommandSender with JsonMessageReader { this: Actor =>

  val chatController: PartialFunction[Any, Unit] = sendWebSocketMessages

  def onCommand(cmd: CommandFromWebSocket) {
    cmd.cmd match {
      case msg: ChatMessage   => self ! BroadCastCommand(ChatMessageCommand(msg))
    }
  }

  val cmdMapping = Map(ChatMessageCommand.name -> classOf[ChatMessage])
  def bodyType(commandType: String) = cmdMapping.get(commandType)
}