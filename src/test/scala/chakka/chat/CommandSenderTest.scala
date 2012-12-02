package chakka.chat

import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.google.gson.GsonBuilder

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class CommandSenderTest extends Specification with ShouldMatchers with Mockito with SocketHelper {

  "When a wrapped command is received it" should {
    "unwrap it and send the message to all filtered people" in {
      val s1 = ChatSocket("s1")
      val c1 = mockOpenCon()
      s1.onOpen(c1)
      val s2 = ChatSocket("s2")
      val c2 = mockOpenCon()
      s2.onOpen(c2)
      val s3 = ChatSocket("s3")
      val c3 = mockOpenCon()
      s3.onOpen(c3)


      val people = Vector(s1, s2, s3)
      val sender = new SimpleCommandSender(people)

      val cmd = ChatMessageCommand(ChatMessage("Hello World!"))

      val wrapper = new CommandWrapper(cmd) {
        def filterRecipients(all: Seq[ChatSocket]) = List(s1, s3)
      }

      sender.sendWebSocketMessages.apply(wrapper)

      val helloJson: String = """{"body":{"msg":"Hello World!"},"name":"msg"}"""
      there were one(c1).sendMessage(helloJson)
      there were one(c3).sendMessage(helloJson)

      there were no(c2).sendMessage(any[String])
    }
  }
}

private class SimpleCommandSender(val people: Vector[ChatSocket]) extends CommandSender with HasGson {
  val gson = new GsonBuilder().create()

}