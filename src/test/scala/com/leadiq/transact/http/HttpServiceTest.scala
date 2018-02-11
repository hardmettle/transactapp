package com.leadiq.transact.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ HttpEntity, MediaTypes }
import com.leadiq.transact.ApiSpec
import com.leadiq.transact.http.route.TransactionRoutes
import com.leadiq.transact.model.{ Sum, Transaction, TransactionId }
import com.leadiq.transact.service.TransactionServiceImpl
import com.leadiq.transact.service.cache.TransactionCache
import com.leadiq.transact.util.Protocol
import spray.json._

import scala.concurrent.Future

class HttpServiceTest extends ApiSpec with Protocol {
  
  val httpService = new HttpService(
    new TransactionRoutes(cache)
  )

  import TestData._

  "service" should {

    s"respond with HTTP-$NotFound for a non existing path" in {
      Get("/non/existing/") ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "The path you requested [/non/existing/] does not exist."
      }
    }

    s"respond with HTTP-$MethodNotAllowed for a non supported HTTP method" in {
      Head(generalUrl()) ~> httpService.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Not supported method! Supported methods are: PUT, GET!"
      }
    }
  }
  "new transaction" should {
    s"respond with HTTP-$OK when registering a new transaction" in {
      val transactionId = transactionIdInfo()
      val transaction = transactionInfo()
      val requestEntity = HttpEntity(MediaTypes.`application/json`, registrationJson(transaction).toString)
      Put(generalUrl(transactionId), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[String] shouldBe "Transaction saved"
      }
    }
  }

  "retrieval" should {

    s"respond with HTTP-$OK for an existing transaction" in {
      val existingTransaction = transactionInfo()
      val existingTransactionId = transactionIdInfo()
      cache.save(existingTransactionId, existingTransaction)

      Get(generalUrl(existingTransactionId)) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[JsValue] shouldBe retrieveJson(existingTransaction)
      }
    }

    s"respond with HTTP-$NotFound for a non existing transaction" in {
      val notExistingId = transactionIdInfo()
      Get(generalUrl(notExistingId)) ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe s"Could not find the transact(s) identified by id : ${notExistingId.transactionId}"
      }
    }
    s"respond with HTTP-$OK for an existing transaction when fetching with types" in {
      val existingTransaction = transactionInfo()
      val existingTransactionId = transactionIdInfo()
      cache.save(existingTransactionId, existingTransaction)

      //println(cache.read(existingTransactionId))
      Get(generalUrlGetFromTypes(existingTransaction.types)) ~> httpService.routes ~> check {
        response.status shouldBe OK
      }
    }
    s"respond with HTTP-$OK for getting sum for transaction id" in {
      val existingTransaction = transactionInfo()
      val existingTransactionId = TransactionId(10)
      val existingTransactionChild = transactionInfo(parentId = Some(existingTransactionId.transactionId))
      val existingTransactionId2 = TransactionId(11)
      val sum = Sum(existingTransaction.amount + existingTransactionChild.amount)

      cache.save(existingTransactionId, existingTransaction)
      cache.save(existingTransactionId2, existingTransactionChild)
      Get(generalUrlGetSum(existingTransactionId)) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[JsValue] shouldBe retrieveSumJson(sum)
      }
    }

  }

  object TestData {

    def registrationJson(transaction: Transaction = transactionInfo()): String =
      if (transaction.parent_id.nonEmpty) {
        s"""
           |{
           |"amount": ${transaction.amount},
           |"types": "${transaction.types}",
           |"parent_id": "${transaction.parent_id.get}"
           |}
           |""".stripMargin.parseJson.toString
      } else {
        s"""
           |{
           |"amount": ${transaction.amount},
           |"types": "${transaction.types}"
           |}
           |""".stripMargin.parseJson.toString
      }

    def retrieveJson(transaction: Transaction): JsValue =
      if (transaction.parent_id.nonEmpty) {
        s"""
           |{
           |"amount": ${transaction.amount},
           |"types": "${transaction.types}",
           |"parent_id": "${transaction.parent_id.get}"
           |}
           |""".stripMargin.parseJson
      } else {
        s"""
           |{
           |"amount": ${transaction.amount},
           |"types": "${transaction.types}"
           |}
           |""".stripMargin.parseJson
      }

    def retrieveSumJson(sum: Sum): JsValue =
      s"""
         |{
         |"sum": ${sum.sum}
         |}
       """.stripMargin.parseJson

    def generalUrl(transactionId: TransactionId = transactionIdInfo()): String =
      s"/v1/transactionservice/transaction/${transactionId.transactionId}/"

    def generalUrlGetFromTypes(transactionTypes: String): String =
      s"/v1/transactionservice/types/$transactionTypes/"

    def generalUrlGetSum(transactionId: TransactionId): String =
      s"/v1/transactionservice/sum/${transactionId.transactionId}/"

  }

}