package com.github.phisgr.akka.demo

import akka.persistence.{PersistentActor, SnapshotOffer}
import com.github.phisgr.akka.demo.proto.calculator.{CalculatorEvent, CalculatorSnapshot}
import com.github.phisgr.akka.demo.proto.calculator.CalculatorEvent.Event

import scala.util.Random

class CalculatorEntity(name: String) extends PersistentActor {

  import com.github.phisgr.akka.demo.CalculatorEntity._

  private var acc = 0

  private def onCommand(c: Command): Unit = {
    val eventWithTime = CalculatorEvent(time = System.currentTimeMillis())
    val event = c match {
      case Add(i) => eventWithTime.withAdd(i)
      case Mul(i) => eventWithTime.withMul(i)
      case Div(i) => eventWithTime.withDiv(i)
      case Reset(to) => eventWithTime.withSetTo(to)
      case AddRandom(lower, upper) =>
        val result = lower + Random.nextInt(upper - lower)
        eventWithTime.withAddRandom(proto.calculator.AddRandom(lowerBound = lower, upperBound = upper, added = result))
    }
    persist(event)(applyEvent)
  }

  private def applyEvent(e: CalculatorEvent): Unit = e.event match {
    case Event.Empty => // ignore
    case Event.Add(value) => acc += value
    case Event.Mul(value) => acc *= value
    case Event.Div(value) => acc /= value
    case Event.SetTo(value) => acc = value
    case Event.AddRandom(value) => acc += value.added
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: CalculatorSnapshot) => acc = snapshot.currentValue
    case e: CalculatorEvent => applyEvent(e)
  }

  override def receiveCommand: Receive = {
    case c: Command => onCommand(c)
    case GetState => sender() ! acc
  }

  override val persistenceId: String = s"calculator-entity-$name"
}

object CalculatorEntity {

  sealed trait Command

  case class Add(i: Int) extends Command

  case class Mul(i: Int) extends Command

  case class Div(i: Int) extends Command

  case class AddRandom(lowerBound: Int, upperBound: Int) extends Command

  case class Reset(to: Int) extends Command

  case object GetState

}
