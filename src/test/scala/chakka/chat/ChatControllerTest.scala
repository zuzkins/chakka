package chakka.chat

import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import com.google.gson.GsonBuilder
import akka.actor.{ActorLogging, Props, ActorSystem, Actor}
import akka.testkit.{TestActorRef, TestKit}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatControllerTest extends TestKit(ActorSystem("ChatControllerTest")) with Specification with ShouldMatchers with SocketHelper {

  val gson = new GsonBuilder().create()

  sequential

  "when a chat message is sent, the controller" should {
    "broadcast it to everyone" in {
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

      val ref: TestActorRef[TestChatController] = TestActorRef(Props[TestChatController])
      val actor = ref.underlyingActor

      actor.people = people

      val msg: Message = Message("Hello All!", "s1")
      actor.onCommand(CommandFromWebSocket(s1.ident, msg))
      val expectJson = gson.toJson(ChatMessageCommand(msg))

      there was one(c1).sendMessage(expectJson)
      there was one(c2).sendMessage(expectJson)
      there was one(c3).sendMessage(expectJson)
    }
  }
  "when the controller doesn't know what to do with a command from websocket, it" should {
    "notify the sender with unknown command message" in {
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

      val ref: TestActorRef[TestChatController] = TestActorRef(Props[TestChatController])
      val actor = ref.underlyingActor

      actor.people = people

      actor.onCommand(CommandFromWebSocket(s2.ident, TestUnknownCommand()))

      val expectJson = gson.toJson(UnknownCommand(Message("Command [TestUnknownCommand] is uknown")))

      there was one(c2).sendMessage(expectJson)
      there was no(c1).sendMessage(any[String])
      there was no(c3).sendMessage(any[String])
    }
  }

  step(system.shutdown())
}

private class TestChatController extends ChatController with Actor with ActorLogging with CommandSender with JsonMessageReader {

  def receive = receiveChatMsgs

  override def onEmptyMessage(sender: SocketIdent) {
    throw new IllegalStateException("Should not receive on empty message from: " + sender)
  }

  override def onInvalidCommand(sender: SocketIdent, msg: String) {
    throw new IllegalStateException("Unexpected invalid command from: " + sender)
  }

  def gson = new GsonBuilder().create()

  var people = Vector[ChatSocket]()
}

private case class TestUnknownCommand(content: String = "Secret")