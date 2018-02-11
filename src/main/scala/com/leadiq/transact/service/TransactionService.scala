package com.leadiq.transact.service

import com.leadiq.transact.model._
import com.leadiq.transact.service.cache.TransactionCache
import com.leadiq.transact.util.ActorContext

import scala.concurrent.Future

//Service to define business logic required for transaction API endpoints
trait TransactionService extends ActorContext {

  //Method that takes transaction id and transaction and saves them to cache
  def save(transactionId: TransactionId, transaction: Transaction): Future[TransactionSaveResult.Value]
  //Method that takes transaction id and fetches corresponding transaction from cache
  def read(transactionId: TransactionId): Future[Option[Transaction]]
  //Method that takes transaction type and fetches corresponding transaction from cache
  def read(transactionType: String): Future[List[TransactionId]]
  //Method that takes transaction id and returns sum of amount of transaction including its sub-transaction
  def readSum(transactionType: TransactionId): Future[Option[Sum]]

}
object TransactionService {
  def apply(): TransactionServiceImpl = new TransactionServiceImpl(TransactionCache())
}
class TransactionServiceImpl(transactionCache: TransactionCache[TransactionId, Transaction]) extends TransactionService {

  override def save(transactionId: TransactionId, transaction: Transaction): Future[TransactionSaveResult.Value] =
    transactionCache.set(transactionId, transaction)

  override def read(transactionId: TransactionId): Future[Option[Transaction]] = {

    println(s"Getting transaction for $transactionId")
    transactionCache.get(transactionId)
  }

  override def read(transactionType: String): Future[List[TransactionId]] =
    Future(transactionCache.cache.filter(transactionWithId => {
      transactionWithId._2.types == transactionType
    }).toMap.keys.toList)

  override def readSum(transactionId: TransactionId): Future[Option[Sum]] =
    transactionCache.get(transactionId).map({
      case Some(parent) =>
        Some(Sum(parent.amount + transactionCache.cache.filter(_._2.parent_id.getOrElse(-1L) == transactionId.transactionId).values.map(_.amount).sum))
      case None => None
    })
}
