package com.leadiq.transact.util

import com.twitter.util.LruMap

//Loads cache config for API
trait CacheConfig[K, V] extends Config {

  val cache: LruMap[K, V]
  def clear(): Unit = {
    cache.clear()
  }
}
