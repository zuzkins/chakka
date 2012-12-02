package chakka.chat

import com.google.gson.{GsonBuilder, Gson}

/**
 * @author Jiri Zuna (jiri@zunovi.cz)
 */

trait HasGson {

  def gson: Gson

}

trait GsonProvider extends HasGson {
  val gson = GsonProvider.gson
}

object GsonProvider {

  val gson = {
    val b = new GsonBuilder().setPrettyPrinting()
    b.create()
  }
}