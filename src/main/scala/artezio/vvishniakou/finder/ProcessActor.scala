package artezio.vvishniakou.finder

import akka.actor.Actor
import artezio.vvishniakou.finder.Messages.{FoundResult, Process}
import spray.http.{StatusCodes, HttpResponse}

class ProcessActor extends Actor {

  def doProcess(httpResponse: HttpResponse, word: String): Int = httpResponse match {
    case HttpResponse(status, _, _, _) if status != StatusCodes.OK => 0

    case _ =>
      val entry = httpResponse.entity.asString
      //      val parser = new XhtmlParser(Source.fromString(entry));
      entry.split(">{1}[^<]*" + word + "[^>]*<{1}".r).size - 1
  }

  def receive = {
    case Process(response, word) => sender() ! FoundResult(doProcess(response, word))

    case _ => println("Unknown message")
  }
}
