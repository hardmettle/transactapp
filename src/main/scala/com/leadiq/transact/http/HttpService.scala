package com.leadiq.transact.http

import akka.http.scaladsl.server.Directives.{ handleRejections, pathPrefix }
import com.leadiq.transact.http.route.TransactionRoutes

//Service class that wraps the routes and provide starting point of the service with Rejection handling
class HttpService(transactionRoutes: TransactionRoutes) extends RejectionHandling {
  val routes =
    handleRejections(customRejectionHandler) {
      pathPrefix("v1") {
        transactionRoutes.transactionRoutes
      }
    }

}

object HttpService {
  def apply(): HttpService =
    new HttpService(TransactionRoutes())
}
