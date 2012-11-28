package chakka.servlet

import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

class ChatRoomNameUrlParserTest extends Specification with ShouldMatchers {

  "Chat room name parser" should {
    "be okay with empty url part" in {
      val p = new ChatRoomNameParser {}

      p.parseChatRoomName(None) should beNone
    }
    "parse the name of the chat room without preceding and trailing '/'" in {
      val p = new ChatRoomNameParser {}

      p.parseChatRoomName(Some("/anything:")) should_== Some("anything:")
      p.parseChatRoomName(Some("/anything/something")) should_== Some("anything")
      p.parseChatRoomName(Some("/1231231/")) should_== Some("1231231")
      p.parseChatRoomName(Some("/asfa!")) should_== Some("asfa!")
      p.parseChatRoomName(Some("/anything")) should_== Some("anything")
      p.parseChatRoomName(Some("anything")) should_== Some("anything")
      p.parseChatRoomName(Some("////")) should beNone;
    }
  }

}