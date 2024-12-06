//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {

  val payloadReg = RegInit(0.U.asTypeOf(new Payload))
  payloadReg.payload(0) := 0.U
  payloadReg.payload(1) := 1.U
  payloadReg.payload(2) := 2.U
  payloadReg.payload(3) := 3.U
  payloadReg.payload(4) := 4.U
  payloadReg.payload(5) := 5.U
  payloadReg.payload(6) := 6.U
  payloadReg.payload(7) := 7.U

  val all = payloadReg.payload.asTypeOf(new MyBundle)
  dontTouch(all)

  // if b=0 AND data =/= 0 - bottom
  // if b=0 AND data === 0 - continue
  // if b=1 AND data=dontcare - continue

  val checkKeys =
    all.key(0).testKey.map { key => key(31, 0).asBools.reduce(_ || _) }
  val reduceAllKeys = checkKeys.reduce(_ || _)

  val checkAllKeys = all.key.map { key =>
    key.testKey
      .map { testKey => testKey(31, 0).asBools.reduce(_ || _) }
      .reduce(_ || _)
  }
  val reduceMoreKeys = checkAllKeys.reduce(_ || _)
  // dontTouch(reduceMoreKeys)
  // dontTouch(checkAllKeys)
  val sig = (0 until 7 by 1)
    .map { i =>
      val checkKey = all
        .key(i)
        .testKey
        .map { testKey => testKey(31, 0).asBools.reduce(_ || _) }
        .reduce(_ || _)
      val checkB = all.key(i).b(0).asBool

      checkKey && !checkB
    }
    .reduce(_ || _)

}
class Payload extends Bundle {
  val payload = Vec(132, UInt(64.W))
}

class GroupBundle extends Bundle {
  val b = UInt(64.W)
  val c = Vec(3, UInt(64.W))
  val d = Vec(4, UInt(64.W))
  val testKey = Vec(8, UInt(64.W))
}
class MyBundle extends Bundle {
  val key = Vec(8, new GroupBundle)
}

class ResponseBundle extends Bundle {
  val t = Vec(2, UInt(64.W))
  val a = UInt(64.W)
  val b = UInt(64.W)
  val c = UInt(64.W)

}
object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
