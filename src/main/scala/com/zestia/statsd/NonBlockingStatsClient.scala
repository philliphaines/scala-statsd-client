package com.zestia.statsd

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

class NonBlockingStatsClient(
  val prefix: String = "", 
  val hostname: String, 
  val port: Integer,
  val errorHandler: StatsErrorHandler = new StatsErrorHandler {
      override def handleException(throwable:  Throwable) {}
    }
  ) extends StatsClient {

  private val clientSocket = new DatagramSocket()
  clientSocket.connect(new InetSocketAddress(hostname, port))

  val executor = Executors.newSingleThreadExecutor(
    new ThreadFactory() {
          val delegate = Executors.defaultThreadFactory()

          override def newThread(r: Runnable): Thread = {
              val result = delegate.newThread(r)
              result.setName("StatsD-" + result.getName)
              result
          }
      }
    )

  override def count(aspect: String, delta: Integer) {
    send(s"$aspect:$delta|c")
  }
  override def increment(aspect: String) = count(aspect, 1)
  override def decrement(aspect: String) = count(aspect, -1)

  override def gauge(aspect: String,value: Integer) {
    send(s"$aspect:$value|g")
  }

  override def gaugeDelta(aspect: String,delta: Integer) {
    if (delta > 0) {
      send(s"$aspect:+$delta|g")
    } else {
      gauge(aspect, delta)
    }
  }

  override def time(aspect: String, timeInMs: Long) {
    send(s"$aspect:$timeInMs|ms")
  }

  override def set(aspect: String, value: Integer) {
    send(s"$aspect:$value|s")
  }

  def stop() {
    try {
          executor.shutdown()
          executor.awaitTermination(30, TimeUnit.SECONDS)
    } catch {
        case throwable: Throwable => errorHandler.handleException(throwable)
    } finally {
      if (clientSocket != null) {
          clientSocket.close()
      }
    }
  }

  private def send(message: String) {
    try {
      executor.execute(new Runnable() {
          override def run() {
              blockingSend(s"$prefix.$message")
          }
      })
    } catch {
      case throwable: Throwable => errorHandler.handleException(throwable)
    }
  }

  private def blockingSend(message: String) {
    val sendData = message.getBytes
    val sendPacket = new DatagramPacket(sendData, sendData.length)
    clientSocket.send(sendPacket)
  } 
}