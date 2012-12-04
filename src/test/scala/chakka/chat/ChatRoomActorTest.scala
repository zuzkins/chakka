package chakka.chat

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import com.google.gson.GsonBuilder

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomActorTest extends TestKit(ActorSystem("ChatRoomActorTest")) with Specification with ShouldMatchers with SocketHelper with GsonProvider {

  sequential

  "when the controller gets the message to list all chat users, it" should {
    "respond with a list of all usernames currently in the chatroom" in {
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

      val ref: TestActorRef[ChatRoomActor] = TestActorRef(Props(new ChatRoomActor("testRoom")))

      for (s <- people) s.registerLifecycleListener(ref)

      val actor = ref.underlyingActor

      actor.people = people

      val cmdJson = gson.toJson(ListUsersCommand)

      s2.onMessage(cmdJson)

      val ulist = UserListCommand(UserList(List("s1", "s2", "s3")))
      val json = gson.toJson(ulist)

      there was no(c1).sendMessage(any[String])
      there was one(c2).sendMessage(json)
      there was no(c3).sendMessage(any[String])
    }
  }


  step(system.shutdown())

}