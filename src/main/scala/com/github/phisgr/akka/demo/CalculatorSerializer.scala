package com.github.phisgr.akka.demo

import akka.serialization.SerializerWithStringManifest
import com.github.phisgr.akka.demo.proto.calculator.{CalculatorEvent, CalculatorSnapshot}
import scalapb.GeneratedMessage

class CalculatorSerializer extends SerializerWithStringManifest {

  import CalculatorSerializer._

  override def identifier: Int = 12345

  override def toBinary(o: AnyRef): Array[Byte] = o.asInstanceOf[GeneratedMessage].toByteArray

  override def manifest(o: AnyRef): String = o match {
    case _: CalculatorEvent => Event
    case _: CalculatorSnapshot => Snapshot
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case Event => CalculatorEvent.parseFrom(bytes)
    case Snapshot => CalculatorSnapshot.parseFrom(bytes)
  }
}

private object CalculatorSerializer {
  val Event = "event"
  val Snapshot = "snapshot"
}
