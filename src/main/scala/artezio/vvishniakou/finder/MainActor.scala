package artezio.vvishniakou.finder

import akka.actor.{Props, Actor}
import artezio.vvishniakou.finder.Messages._

class MainActor extends Actor {

  private var countFounders = 0
  private var countThreads = 0
  private var word = ""

  def receive = {
    case ResponseSuccess(response) =>
      val process = context.system.actorOf(Props[ProcessActor])
      process ! Process(response, word)

    case FoundResult(x) =>
      context.stop(sender())
      countThreads -= 1
      countFounders += x
      if (countThreads == 0) {
        println(word + " found " + countFounders + " times")
        Finder.shutdown()
      }

    case CountThreads(x) => countThreads = x

    case Word(value) => this.word = value

    case _ => println("Unknown message")
  }
}