package com.leadiq.transact.services

import com.leadiq.transact.UnitSpec
import com.leadiq.transact.model._
import com.leadiq.transact.service.{ TransactionService, TransactionServiceImpl }
import com.leadiq.transact.service.cache.TransactionCacheImpl

import scala.concurrent.Future

class TransactionServiceTest extends UnitSpec {
  private val existingTransaction = transactionInfo()
  private val nonExistingTransactionId = transactionIdInfo()
  private val existingTransactionId = transactionIdInfo()

  "read" should {

    "read an existing entry" in new TestFixture {
      service.read(existingTransactionId).futureValue.value shouldBe (existingTransaction)
    }

    "return NONE for a non existing entry" in new TestFixture {
      service.read(nonExistingTransactionId).futureValue shouldBe None
    }
  }
  "readSum" should {

    "return total sum for existing transaction" in new TestFixture {
      service.readSum(existingTransactionId).futureValue.value shouldBe Sum(existingTransaction.amount)
    }
  }
  "save" should {
    import TransactionSaveResult._
    s"return $SaveSuccessful for a new entry" in new TestFixture {
      service.save(existingTransactionId, existingTransaction).futureValue shouldBe SaveSuccessful
    }
  }

  trait TestFixture {
    val cacheStub = stub[TransactionCacheImpl[TransactionId, Transaction]]
    val service = new TransactionServiceImpl(cacheStub)
    cacheStub.get _ when existingTransactionId returns Future.successful(Some(existingTransaction))
    cacheStub.get _ when nonExistingTransactionId returns Future(None)
    cacheStub.set _ when (existingTransactionId, existingTransaction) returns Future.successful(TransactionSaveResult.SaveSuccessful)
  }

}
