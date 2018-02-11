package com.leadiq.transact.helpers

import com.leadiq.transact.model.{ Transaction, TransactionId }

import scala.util.Random

trait TestData {

  def transactionInfo(amount: Double = randomDouble(), types: String = randomString(), parentId: Option[Long] = None): Transaction = Transaction(amount, types, parentId)
  def transactionIdInfo(): TransactionId = TransactionId(randomLong())
  def randomString(): String = Random.alphanumeric.take(10).mkString("")
  def randomDouble(): Double = Math.abs(Random.nextDouble())
  def randomLong(): Long = Math.abs(Random.nextLong())
}
