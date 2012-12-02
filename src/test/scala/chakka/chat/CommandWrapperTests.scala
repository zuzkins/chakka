package chakka.chat

import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class CommandWrapperTests extends Specification with ShouldMatchers with SocketHelper {

  val cmd = ChatMessageCommand(ChatMessage("Hello World!"))

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

  "Broadcast command wrapper" should {
    "be sent to all recipients" in {
      val wrapper = BroadCastCommand(cmd)

      wrapper.filterRecipients(people) should_== people
    }
  }
  "Broadcast to others command wrapper" should {
    "be sent only to recipients who are not sender" in {
      val all = people ++: Vector(s2, s2, s2)

      BroadCastToOthersCommand(s2.ident, cmd).filterRecipients(all) should_== Vector(s1, s3)
    }
  }
  "Private command wrapper" should {
    "be sent only to websockets owned by users with requested usernames" in {
      PrivateCommand("s2", cmd).filterRecipients(people) should_== Vector(s2)
    }
  }

}