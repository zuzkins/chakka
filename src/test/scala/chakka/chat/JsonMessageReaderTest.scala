package chakka.chat

import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import org.specs2.mock.Mockito

import org.mockito.{Mockito => Mock}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class JsonMessageReaderTest extends Specification with ShouldMatchers with Mockito {

  "When an empty message or message without type is received, the parser" should {
    "notify me with a sender id" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p)

      val id = SocketIdent("Frankie")

      r.onMessage(id, null)
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, "")
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, "   ")
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, """{}""")
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, """{"unknown": "is unknown"}""")
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, """{"name": null}""")
      there was one(p).onEmptyMessage(id)

      Mock.reset(p)

      r.onMessage(id, """{"name": "  "}""")
      there was one(p).onEmptyMessage(id)
    }
  }
  "Reading a command with an unknown type" should {
    "notify the commandProcessor" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p, None)

      val id = SocketIdent("Frankie")

      r.onMessage(id, """{"name": "join", "body": "Fredie"}""")

      there was one(p).onUnknownCommand(id, "join")
    }
  }
  "When a message with invalid body is received, it" should {
    "notify the commandProcessor when the body is not an object" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p, Some(classOf[TestJoinMsg]))

      val id = SocketIdent("Frankie")

      r.onMessage(id, """{"name": "join", "body": "Fredie"}""")

      there was one(p).onInvalidCommand(id, "the command body must be an object, or empty")
    }
  }
  "When a command is read, it" should {
    "require the body to be present, when its not a NoBodyCommand type of command" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p, Some(classOf[TestJoinMsg]))

      val id = SocketIdent("Frankie")
      r.onMessage(id, """{"name": "join", "body": null}""")
      there was one(p).onInvalidCommand(id, "the command \"join\" requires command body to be sent")
    }
    "dispatch the sent command name as the command, when the command has no body" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p, Some(NoBodyCommand.getClass))

      val id = SocketIdent("Frankie")
      r.onMessage(id, """{"name": "join", "body": null}""")
      there was one(p).onCommand(CommandFromWebSocket(id, "join"))
    }
    "read the body and dispatch it as the command" in {
      val p = mock[CommandProcessor]
      val r = new SimpleJsonMessageReader(p, Some(NoBodyCommand.getClass))

      val id = SocketIdent("Frankie")
      r.onMessage(id, """{"name": "join", "body": {"name": "Frenkie", "password": "hollywood"}""")
      there was one(p).onCommand(CommandFromWebSocket(id, TestCommand("Frenkie", "hollywood")))
    }
  }
}

private class SimpleJsonMessageReader(val processor: CommandProcessor, val bodyClass: Option[Class[_]] = None) extends JsonMessageReader with CommandProcessor with GsonProvider {

  def onEmptyMessage(sender: SocketIdent) {processor.onEmptyMessage(sender)}

  def bodyType(commandType: String) = bodyClass

  def onInvalidCommand(sender: SocketIdent, msg: String) {processor.onInvalidCommand(sender, msg)}

  def onUnknownCommand(sender: SocketIdent, commandType: String) {processor.onUnknownCommand(sender, commandType)}

  def onCommand(cmd: CommandFromWebSocket) {processor.onCommand(cmd)}
}

private case class TestJoinMsg(name: String)
private case class TestCommand(name: String, password: String)