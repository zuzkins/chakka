package chakka

import akka.testkit.TestKit
import akka.actor.{Props, ActorSystem}
import chat.{ChatSocket, JoinRoom, ChatRoomRegistry}
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import akka.pattern.ask
import akka.util.Timeout

import concurrent.Await
import org.specs2.time.NoTimeConversions

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class FullJoinChatTest extends TestKit(ActorSystem("FullJoinChatTest")) with Specification with ShouldMatchers with NoTimeConversions {

  sequential

  "Trying to join a room".should {
    "return hand me back working websocket" in {
      val ref = system.actorOf(Props[ChatRoomRegistry])

      val wsFuture = ask(ref, JoinRoom("Frankie", "Hollywood Fans"))(Timeout(1.second)).mapTo[ChatSocket]

      val ws = Await.result(wsFuture, Timeout(1.second).duration)

      ws should not beNull;
      
      ws.username should_== "Frankie"
    }
  }

  step(system.shutdown())
}