package chakka.chat

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */
trait CommandSender extends HasClients { this: HasGson =>

  val sendWebSocketMessages: PartialFunction[Any, Unit] = {
    case c: CommandWrapper  => sendCommand(c)
  }

  def sendCommand(wrapped: CommandWrapper) {
    val json = gson.toJson(wrapped.command)
    for (p <- wrapped.filterRecipients(people)) p.sendMessage(json)
  }
}

trait HasClients {
  def people: Vector[ChatSocket]
}