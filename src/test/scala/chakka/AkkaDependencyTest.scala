package chakka

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Props, Actor, ActorSystem}
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class AkkaDependencyTest extends TestKit(ActorSystem("AkkaDependencyTest")) with Specification with ShouldMatchers {

  sequential

  "ActorSystem" should {
    "be alive and working correctly" in {
      val ref: TestActorRef[Counter] = TestActorRef(Props[Counter])
      val actor = ref.underlyingActor

      actor.counter should_== 0

      ref ! Count

      actor.counter should_== 1
    }
  }

  step(system.shutdown())


}

private class Counter extends Actor {
  var counter = 0
  def receive = {
    case Count    => counter = counter + 1
  }
}

private object Count