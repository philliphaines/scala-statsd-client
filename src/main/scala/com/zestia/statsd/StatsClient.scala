package com.zestia.statsd

trait StatsClient {
  def count(aspect: String, delta: Integer)
  def increment(aspect: String)
  def decrement(aspect: String)

  def gauge(aspect: String, value: Integer)
  def gaugeDelta(aspect: String, delta: Integer)

  def time(aspect: String, timeInMs: Long)  

  def set(aspect: String, value: Integer)
}