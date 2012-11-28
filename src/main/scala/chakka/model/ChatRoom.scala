package chakka.model

import akka.actor.ActorRef

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

case class ChatRoom(name: String, actor: ActorRef)