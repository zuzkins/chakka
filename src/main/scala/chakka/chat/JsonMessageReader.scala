package chakka.chat

import com.google.gson.{JsonElement, JsonParser}
import java.io.{Reader, StringReader}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

trait JsonMessageReader extends HasGson { this: CommandProcessor =>

  def onMessage(sender: SocketIdent, msgText: String) {
    val msg = Option(msgText).map(_.trim)

    msg match {
      case None       => onEmptyMessage(sender)
      case Some("")   => onEmptyMessage(sender)
      case Some(x)    => parseCommand(new StringReader(x), sender)
    }

    def parseCommand(from: Reader, sender: SocketIdent) {
      val parser = new JsonParser()
      val parsed = parser.parse(from)

      val cmdType = getCommandName(parsed)

      cmdType match {
        case None     =>  onEmptyMessage(sender)
        case Some(t)  =>  processBody(sender, t, parsed.getAsJsonObject.get("body"))
      }
    }
  }

  def parseBody(sender: SocketIdent, commandType: String, bodyType: Class[_], body: JsonElement) {
    val needsBody = bodyType != NoBodyCommand.getClass
    val isEmptyBody = body == null || body.isJsonNull

    if (needsBody && isEmptyBody) {
      onInvalidCommand(sender, "the command \"" + commandType + "\" requires command body to be sent")
    } else if (!needsBody) {
      dispatchCommandFrom(sender, commandType)
    } else {
      val payload: AnyRef = gson.fromJson[AnyRef](body, bodyType)
      dispatchCommandFrom(sender, payload)
    }
  }

  def processBody(sender: SocketIdent, commandType: String, body: JsonElement) {
    val bodyT = bodyType(commandType)

    if (bodyT == None) {
      onUnknownCommand(sender, commandType)
    } else if (!validBody(body)) {
      onInvalidCommand(sender, "the command body must be an object, or empty")
    } else {
      parseBody(sender, commandType, bodyT.get, body)
    }
  }

  private def dispatchCommandFrom(sender: SocketIdent, cmd: AnyRef) {
    onCommand(CommandFromWebSocket(sender, cmd))
  }

  private def getCommandName(root: JsonElement): Option[String] = {
    if (root == null || root.isJsonNull || !root.isJsonObject)
      None
    else {
      val el: JsonElement = root.getAsJsonObject.get("name")
      if (el == null || el.isJsonNull)
        None
      else if (el.getAsString.trim == "")
        None
      else
        Some(el.getAsString)
    }
  }

  def validBody(e: JsonElement) = {
    e == null || e.isJsonNull || e.isJsonObject
  }

}


trait CommandProcessor {
  def onEmptyMessage(sender: SocketIdent)

  def onInvalidCommand(sender: SocketIdent, msg: String)
  def onUnknownCommand(sender: SocketIdent, commandType: String)

  def bodyType(commandType: String): Option[Class[_]]

  def onCommand(cmd: CommandFromWebSocket)
}