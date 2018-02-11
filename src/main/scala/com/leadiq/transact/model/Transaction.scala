package com.leadiq.transact.model

//Model that represent transaction id
case class TransactionId(transactionId: Long)

//class representing a transaction. It handles validation of fields as well
case class Transaction(amount: Double, types: String, parent_id: Option[Long]) {
  require(amount > 0.0, "Amount should be positive")
  require(types.nonEmpty, "Transaction type should not be empty")
}

//Class that represents a sum when sent as a response
case class Sum(sum: Double)

//Wraps the transaction saving process result
object TransactionSaveResult extends Enumeration {
  val SaveSuccessful, SaveFailure = Value
}

