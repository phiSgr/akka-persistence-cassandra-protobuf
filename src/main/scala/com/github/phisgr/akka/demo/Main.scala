package com.github.phisgr.akka.demo

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.phisgr.akka.demo.CalculatorEntity._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val actorSystem = ActorSystem("example")
  implicit val timeout = Timeout(5.seconds)

  Thread.currentThread().setUncaughtExceptionHandler { (t: Thread, e: Throwable) =>
    // mimic the default behaviour in ThreadGroup#uncaughtException
    System.err.print(s"""Exception in thread "${t.getName}" """)
    e.printStackTrace(System.err)
    // then terminate the actor system to exit the JVM
    actorSystem.terminate()
  }
  val persistentActor = actorSystem.actorOf(Props(classOf[CalculatorEntity], "1"), "calculatorActor-1")

  def printState(): Unit = {
    val res = persistentActor ? GetState
    println(s"state is now ${Await.result(res, Duration.Inf)}")
  }

  persistentActor ! Add(5)
  persistentActor ! Add(5)
  persistentActor ! Add(5)
  printState()

  persistentActor ! Div(3)
  printState()

  persistentActor ! Div(0)
  printState()

  Await.result(actorSystem.terminate(), Duration.Inf)
}
