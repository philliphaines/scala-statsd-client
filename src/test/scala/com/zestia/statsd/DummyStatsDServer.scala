package com.zestia.statsd

import scala.collection.mutable.ArrayBuffer
import java.net.DatagramPacket
import java.net.DatagramSocket


class DummyStatsDServer(val port: Integer) {
  val messages = new ArrayBuffer[String]()
  private val server = new DatagramSocket(port)

  new Thread(new Runnable() {
    override def run() = {
      try {
        val packet = new DatagramPacket(new Array[Byte](256), 256)
        server.receive(packet)
        messages += new String(packet.getData).trim()
        server.close()  

        // need to sleep to release port  
        Thread.sleep(50)                   
      } catch {
        case _: Throwable =>
      }
    }
  }).start()

  def waitForMessage() {
    while (messages.isEmpty) {
      Thread.sleep(50)
    }
  }
}