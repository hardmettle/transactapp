package com.leadiq.transact.http.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.leadiq.transact.model.{ Transaction, TransactionId }
import com.leadiq.transact.service.TransactionService
import com.leadiq.transact.util.Protocol

//All the routes for the API represented by this class.
//The endpoints for the API are defined here
class TransactionRoutes(transactionService: TransactionService) extends Protocol {

  val transactionRoutes = pathPrefix("transactionservice") {
    pathPrefix("transaction" / LongNumber) { transactionId =>
      pathEndOrSingleSlash {
        put {
          entity(as[Transaction]) { transaction =>
            saveTransaction(TransactionId(transactionId), transaction)
          }
        } ~ get {
          retrieveTransaction(TransactionId(transactionId))
        }
      }
    } ~ pathPrefix("types" / Segment) { types =>
      pathEndOrSingleSlash {
        get {
          retrieveTransactionFromType(types)
        }
      }
    } ~ pathPrefix("sum" / LongNumber) { transactionId =>
      pathEndOrSingleSlash {
        get {
          retrieveTransactionSum(TransactionId(transactionId))
        }
      }
    }
  }

  private def saveTransaction(transactionId: TransactionId, transaction: Transaction): Route = {
    val saveResult = transactionService.save(transactionId, transaction)
    import com.leadiq.transact.model.TransactionSaveResult._
    onSuccess(saveResult) {
      case SaveSuccessful => complete("Transaction saved")
      case SaveFailure => complete(Conflict, "Transaction save failed")
    }
  }

  private def retrieveTransaction(transactionId: TransactionId): Route = {
    val transaction = transactionService.read(transactionId)
    onSuccess(transaction) {
      case Some(x) => complete(x)
      case None => notFoundFromId(transactionId)
    }
  }

  private def retrieveTransactionFromType(types: String): Route = {
    val transactions = transactionService.read(types)
    onSuccess(transactions) {
      case list: List[TransactionId] => complete(list)
      case _ => notFoundFromType(types)
    }
  }

  private def retrieveTransactionSum(transactionId: TransactionId): Route = {
    val sum = transactionService.readSum(transactionId)
    onSuccess(sum) {
      case Some(x) => complete(x)
      case None => notFoundFromId(transactionId)
    }
  }

  private def notFoundFromId(transactionId: TransactionId): Route = {
    complete(NotFound, s"Could not find the transact(s) identified by id : ${transactionId.transactionId}")
  }

  private def notFoundFromType(types: String): Route = {
    complete(NotFound, s"Could not find the transact having types: $types")
  }
}

object TransactionRoutes {
  def apply(): TransactionRoutes = new TransactionRoutes(TransactionService())
}
