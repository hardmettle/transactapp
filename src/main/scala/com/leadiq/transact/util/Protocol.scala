package com.leadiq.transact.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.leadiq.transact.model.{ Sum, Transaction, TransactionId }
import spray.json.DefaultJsonProtocol

//Represent implicit handling of serialization/deserialization of JSON being used in http request response.
trait Protocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val transactionFormat = jsonFormat3(Transaction)
  implicit val transactionIdFormat = jsonFormat1(TransactionId)
  implicit val sumFormat = jsonFormat1(Sum)

}
