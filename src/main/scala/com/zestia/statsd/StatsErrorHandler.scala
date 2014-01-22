package com.zestia.statsd

trait StatsErrorHandler {
  def handleException(throwable: Throwable)
}