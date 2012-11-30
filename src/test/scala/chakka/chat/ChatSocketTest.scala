package chakka.chat

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import org.specs2.mock.Mockito
import org.specs2.time.NoTimeConversions
import org.eclipse.jetty.websocket.WebSocket.Connection

import concurrent.duration._
import akka.util.Timeout

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatSocketTest extends TestKit(ActorSystem("ChatSocketTest")) with Specification with ShouldMatchers
  with Mockito with NoTimeConversions with SocketHelper {

  implicit val timeout = Timeout(100 millis)

  sequential

  "when createing a chat socket, it" should {
    "remember the username and generate random UUID" in {
      val s: ChatSocket = ChatSocket("xxxOOOxxx")
      s.ident.username should_== "xxxOOOxxx"
      s.ident.id should not beNull
    }
  }
  "when someone registers as listener, it" should {
    "dispatch the SocketActivated message to a listening actor" in {
      val s = ChatSocket("xxxxOOOxxx")

      s.registerLifecycleListener(testActor)

      val c = mockOpenCon()

      s.onOpen(c)

      expectMsg(SocketActivated(s.ident))

      success
    }
    "dispatch the SocketClosed message to a listening actor, when the socket is closed" in {
      val s = ChatSocket("xxxxOOOxxx")

      s.registerLifecycleListener(testActor)

      val c = mockOpenCon()

      s.onClose(1, ":(")

      expectMsg(SocketClosed(s.ident))
      success
    }
    "notify him with the message sent from the websocket" in {
      val s = ChatSocket("xxxxOOOxxx")
      s.registerLifecycleListener(testActor)

      val c = mockOpenCon()

      val msg: String = """{"name": "Frankie }"""
      s.onMessage(msg)

      expectMsg(MessageReceived(msg, s.ident))
      success
    }
  }

  step(system.shutdown())
}

trait SocketHelper extends Mockito {

  def mockOpenCon(): Connection = {
    val c: Connection = mock[Connection]
    c.isOpen returns true
  }
}