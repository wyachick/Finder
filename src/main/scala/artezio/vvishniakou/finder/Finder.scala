package artezio.vvishniakou.finder

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import artezio.vvishniakou.finder.Messages._
import spray.can.Http
import spray.client.pipelining._
import spray.http._
import spray.util._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Finder extends App {
  implicit val system = ActorSystem("finder-system")
  implicit val timeout = Timeout(5.seconds)

  import system.dispatcher

  val mainActor = system.actorOf(Props[MainActor])

  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.stop(mainActor)
    system.shutdown()

  }

  args match {
    case Array() =>
      println("Using command as Finder <word_1, ..., word_N>, where word_X - word to find")
      shutdown()
    case _ =>
      val word = args(0)

      mainActor ! CountThreads(Sources.URLs.size)
      mainActor ! Word(word)

      val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
      val responses: Seq[Future[HttpResponse]] = Sources.URLs.map(url => pipeline(Get(url)))

      responses.foreach(_ onComplete {
        case Success(response) =>
          mainActor ! ResponseSuccess(response)

        case Failure(error) =>
          println(error)
      })
  }
}