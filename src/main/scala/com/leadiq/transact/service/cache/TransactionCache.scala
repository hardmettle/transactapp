package com.leadiq.transact.service.cache

import com.leadiq.transact.model.TransactionSaveResult
import com.leadiq.transact.util.{ ActorContext, CacheConfig }
import com.twitter.util.LruMap

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

//Trait that creates a caching service to store transaction related data
private[service] trait TransactionCache[K, V] extends CacheConfig[K, V] with ActorContext {

  //LRU cache
  override val cache: LruMap[K, V] = new LruMap[K, V](cacheMaxSize)

  //Set value V for key K in cache
  def set(key: K, value: V): Future[TransactionSaveResult.Value]
  //Get value V for key K in cache
  def get(key: K): Future[Option[V]]

}

//Concrete implementation of trait cache
class TransactionCacheImpl[K, V] extends TransactionCache[K, V] {

  override def set(key: K, value: V): Future[TransactionSaveResult.Value] = Future {
    println(s"Saving $key : $value")
    Try { cache.put(key, value) } match {
      case Success(_) => TransactionSaveResult.SaveSuccessful
      case Failure(_) => TransactionSaveResult.SaveFailure
    }
  }

  override def get(key: K): Future[Option[V]] = Future(cache.get(key))

}

object TransactionCache {
  def apply[K, V](): TransactionCacheImpl[K, V] = new TransactionCacheImpl()
}

