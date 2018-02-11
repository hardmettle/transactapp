package com.leadiq.transact

import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import com.leadiq.transact.http.HttpService
import com.leadiq.transact.util.{ ActorContext, Config }

//Main class that is the entry point of application. Starts up the server with endpoint routing and host and port.
object Main extends App with Config with ActorContext {
  val log: LoggingAdapter = Logging(system, getClass)
  val httpService = HttpService()

  Http().bindAndHandle(httpService.routes, httpInterface, httpPort)
}
