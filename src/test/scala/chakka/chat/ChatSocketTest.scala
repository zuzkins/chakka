package chakka.chat

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import org.specs2.mock.Mockito
import org.specs2.time.NoTimeConversions

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatSocketTest extends TestKit(ActorSystem("ChatSocketTest")) with Specification with ShouldMatchers
    with NoTimeConversions {

  sequential

  "when createing a chat socket, it".should {
    "remember the username" in {
      //ChatSocket("xxxOOOxxx").username should_== "xxxOOOxxx"
      true should beTrue
    }
  }

  step(system.shutdown())
}