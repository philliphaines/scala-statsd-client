package com.zestia.statsd

class NoOpStatsClient extends StatsClient {
  override def count(aspect: String, delta: Integer) {}
  override def increment(aspect: String) {}
  override def decrement(aspect: String) {}
  override def gauge(aspect: String, value: Integer) {}
  override def gaugeDelta(aspect: String, delta: Integer) {}
  override def time(aspect: String, timeInMs: Long) {}
  override def set(aspect: String, value: Integer) {}
}
