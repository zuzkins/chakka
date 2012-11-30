package chakka

import akka.testkit.TestKit
import akka.actor.{Props, ActorSystem}
import chat.{ChatSocket, JoinRoom, ChatRoomRegistry}
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import akka.pattern.ask
import akka.util.Timeout

import concurrent.{Future, Await}
import concurrent.duration._

import org.specs2.time.NoTimeConversions

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class FullJoinChatTest extends TestKit(ActorSystem("FullJoinChatTest")) with Specification with ShouldMatchers with NoTimeConversions {

  implicit val timeout = Timeout(1 seconds)

  sequential

  "Trying to join a room".should {
    "return hand me back working websocket" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])

      val wsFuture: Future[ChatSocket] = ask(ref, JoinRoom("Hollywood Fans", "Frankie")).mapTo[ChatSocket]

      val ws = Await.result(wsFuture, timeout.duration)

      ws should not beNull;

      ws.ident.username should_== "Frankie"
    }
  }

  step(system.shutdown())
}