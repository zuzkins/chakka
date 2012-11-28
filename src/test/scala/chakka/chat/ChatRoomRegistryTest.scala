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
    "start with no open chat rooms" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])
      val future = ask(ref, ListRooms).mapTo[List[ChatRoom]]
      val res = Await.result(future, timeout.duration)

      res should_== Nil
    }
    "create the chat room after someone joins it" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])

      val chatRoomName: String = "xxxOOOxxx"
      ref ! JoinRoom(chatRoomName, "Frankie")

      val future = ask(ref, ListRooms).mapTo[List[ChatRoom]]

      val res = Await.result(future, timeout.duration)

      res should not beEmpty;

      res.map(_.name) should_== List(chatRoomName)
    }
    "not recreate the chat room when someone joins already existing room" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])

      val chatRoomName = "xxxOOOxxx"
      ref ! JoinRoom(chatRoomName, "Frankie")
      ref ! JoinRoom(chatRoomName, "Hollywood")

      val future = ask(ref, ListRooms).mapTo[List[ChatRoom]]

      val res = Await.result(future, timeout.duration)

      res should not beEmpty;

      res.map(_.name) should_== List(chatRoomName)
    }
  }

  step(system.shutdown())
}