package artezio.vvishniakou.finder

import spray.http.HttpResponse



object Messages {

  sealed trait Message

  case class FoundResult(count: Int) extends Message
  case class CountThreads(count: Int) extends Message
  case class Word(word: String) extends Message
  case class Process(httpResponse: HttpResponse, word: String) extends Message
  case class ResponseSuccess(httpResponse: HttpResponse) extends Message
  object Stop extends Message

}
