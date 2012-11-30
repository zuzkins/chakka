package chakka.chat

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.{Actor, ActorLogging, Props, ActorSystem}
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import org.specs2.mock.Mockito
import org.specs2.time.NoTimeConversions
import akka.util.Timeout

import concurrent.duration._
import akka.pattern.ask
import concurrent.{Await, Future}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomManagerTest extends TestKit(ActorSystem("ChatRoomManagerTest")) with Specification with ShouldMatchers with Mockito
  with ImplicitSender with NoTimeConversions with SocketHelper {

  implicit val timeout = Timeout(750 millis)

  sequential

  "Joining a managed chat room" should {
    "create the websocket and remember the person who joined" in {
      val ref: TestActorRef[ChatRoomActor] = TestActorRef(Props[ChatRoomActor])
      val actor = ref.underlyingActor


      actor.people should beEmpty;

      ref ! JoinRoom("room", "user")

      actor.people.map(_.ident.username) should_== Vector("user")

      ref ! JoinRoom("room", "user")

      actor.people.map(_.ident.username) should_== Vector("user", "user")
    }
  }
  "When creating a websocket, the chat romm manager" should {
    "register the delegating actor as the listener for this websocket" in {
      val ref: TestActorRef[ChatRoomManager] = TestActorRef(Props(
        new ChatRoomManager with ActorLogging with ChatSocketFactory {
          val eventTarget = testActor

          override def createSocketFor(username: String): ChatSocket = {
            mock[ChatSocket]
          }
        }
      ))

      val actor = ref.underlyingActor

      actor.people should beEmpty;

      val f: Future[ChatSocket] = (ref ? JoinRoom("room", "user")).mapTo[ChatSocket]
      val s = Await.result(f, timeout.duration)

      there was one(s).registerLifecycleListener(testActor)
    }
  }

  "When the underlying connection of a websocket is closed, the manager" should {
    "throw away the websocket" in {
      val ref: TestActorRef[ChatRoomActor] = TestActorRef(Props[ChatRoomActor])
      val actor = ref.underlyingActor

      actor.people should beEmpty;

      val f: Future[ChatSocket] = (ref ? JoinRoom("room", "user")).mapTo[ChatSocket]
      val s = Await.result(f, timeout.duration)


      val c = mockOpenCon()
      s.onOpen(c)

      s.onClose(1, ":(")

      actor.people should beEmpty
    }
  }

  step(system.shutdown())

}