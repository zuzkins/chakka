package chakka.chat

import org.slf4j.LoggerFactory

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
trait CommandSender extends HasClients { this: HasGson =>

  val localLog = LoggerFactory.getLogger(this.getClass)

  val sendWebSocketMessages: PartialFunction[Any, Unit] = {
    case c: CommandWrapper  => sendCommand(c)
  }

  def sendCommand(wrapped: CommandWrapper) {
    val json = gson.toJson(wrapped.command)
    val rec = wrapped.filterRecipients(people)

    localLog.debug("Sending command [" + wrapped.command.name + "] to: " + rec.mkString(", "))
    for (p <- rec) p.sendMessage(json)
  }
}

trait HasClients {
  def people: Vector[ChatSocket]
}