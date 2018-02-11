package com.leadiq.transact.util

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

//Utility that loads actor system,executor and materializer for the api
trait ActorContext {
  implicit val system = ActorSystem("transact-app")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
