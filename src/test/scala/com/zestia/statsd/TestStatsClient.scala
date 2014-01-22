package com.zestia.statsd

import org.specs2.mutable._

class TestStatsClient extends Specification {
  val ServerPort = 17254
  val errorHandler = new StatsErrorHandler {
  var handledException: Option[Throwable] = None
      override def handleException(throwable: Throwable) {
          handledException = Some(throwable)
      }
  }
  val client = new NonBlockingStatsClient("test.prefix", "localhost", ServerPort, errorHandler)
    
  sequential

  "a counter" should {
    "when set to 24 return test.prefix.mycount:24|c" in {
      val server = new DummyStatsDServer(ServerPort)
      client.count("mycount", 24)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mycount:24|c"
    }

    "then incremented return test.prefix.mycount:1|c" in { 
      val server = new DummyStatsDServer(ServerPort)
      client.increment("mycount")         

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mycount:1|c"
    }   
    
    "then decremented return test.prefix.mycount:-1|c" in {
      val server = new DummyStatsDServer(ServerPort)
      client.increment("mycount")         

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mycount:1|c"        
    }
  }   

  "a gauge" should {
    "when set to 24 return test.prefix.mygauge:24|g" in {
      val server = new DummyStatsDServer(ServerPort)
      client.gauge("mygauge", 24)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mygauge:24|g"
    }

    "then set with delta +1 return return test.prefix.mygauge:+1|g" in {
      val server = new DummyStatsDServer(ServerPort)
      client.gaugeDelta("mygauge", 1)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mygauge:+1|g"
    }

    "then set with delta -1 return return test.prefix.mygauge:-1|g" in {
      val server = new DummyStatsDServer(ServerPort)
      client.gaugeDelta("mygauge", -1)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mygauge:-1|g"
    }
  }

  "a timer" should {
    "when timing a 1 second operation return test.prefix.timer:1000|ms" in {
      val server = new DummyStatsDServer(ServerPort)
      client.time("mytimer", 1000)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mytimer:1000|ms"        
    }
  }

  "a set" should {
    "when value is set to 24 return test.prefix.timer:24|s" in {
      val server =  new DummyStatsDServer(ServerPort)
      client.set("mysetter", 24)

      server.waitForMessage()
      server.messages(0) must be equalTo "test.prefix.mysetter:24|s"
    }
  }

  "an error handler" should {
    "empty when there have been no errors" in {
      errorHandler.handledException must beNone
    }
    "contain an exception after an error" in {
      client.stop()
      client.count("error", 10)
      errorHandler.handledException must beSome
    }
  }

  step {
    // clean up at the end
    client.stop()
  }
}