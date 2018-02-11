package com.leadiq.transact

import com.leadiq.transact.model.{ Transaction, TransactionId }
import com.leadiq.transact.service.{ TransactionService, TransactionServiceImpl }
import com.leadiq.transact.service.cache.TransactionCache
import org.scalatest.BeforeAndAfterAll

trait IntegrationSpec extends BaseSpec with BeforeAndAfterAll {
  val cache = new TransactionServiceImpl(TransactionCache[TransactionId, Transaction]())
}
