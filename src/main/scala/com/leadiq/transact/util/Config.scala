package com.leadiq.transact.util

import com.typesafe.config.ConfigFactory

//Utility that loads configurations related to API
trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")

  val cacheMaxSize = config.getInt("cache.max-size")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")
}
