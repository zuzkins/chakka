package chakka.chat

import akka.testkit.TestKit
import akka.actor.{Props, ActorSystem}
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import org.eclipse.jetty.websocket.WebSocket
import concurrent.Await

import chakka.chat._
import chakka.model.ChatRoom

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomRegistryTest extends TestKit(ActorSystem("ChatRoomRegistryTest")) with Specification with ShouldMatchers
    with NoTimeConversions {

  implicit val timeout = Timeout(1 second)

  sequential

  "ChatRoom Registry" should {
    "know all currently open chat rooms" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])
      val future = ask(ref, ListRooms).mapTo[List[ChatRoom]]
      val res = Await.result(future, timeout.duration)

      res should_== Nil
    }
  }

  step(system.shutdown())
}