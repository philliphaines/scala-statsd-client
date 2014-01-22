# Scala Statsd Client Libaray
Use this to commuicate with a statsd server to record statistics from your Application. 
A simple statsd libary implemented in Scala based on [java-statsd-client](https://github.com/youdevise/java-statsd-client).

[![Build Status](https://travis-ci.org/philliphaines/scala-statsd-client.png?branch=master)](https://travis-ci.org/philliphaines/scala-statsd-client)

## Download
Todo - publish on an artifactory

## Usage
```scala
import com.zestia.statsd.NonBlockingStatsClient

object Foo {
	val statistics = new new NonBlockingStatsClient("my.prefix", "statsd-host", 8125)

  def main(args: Array[String]) {
    statistics.increment("bar");
    statistics.time("baz", 100);
    statistics.time("bag", 25);
  } 	   
}
```