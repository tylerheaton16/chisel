//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  val msgAuth = RegInit(VecInit(Seq.fill(4)(0.U(64.W))))
  msgAuth(0) := 15.U
  msgAuth(1) := 32.U
  msgAuth(2) := 8.U
  msgAuth(3) := 26.U

  val keyRef = 0.U((168 * 8).W)
  val ipadRef = 54.U(8.W)
  val opadRef = 92.U(8.W)

  val ipad = VecInit(Seq.fill(199)(ipadRef)).foldLeft(ipadRef) {
    (acc, element) =>
      Cat(acc, element)
  }

  val opad = VecInit(Seq.fill(199)(opadRef)).foldLeft(opadRef) {
    (acc, element) =>
      Cat(acc, element)
  }
  dontTouch(ipad)
  dontTouch(opad)

  //Note: key is now appended with `keyRef` zeros
  val key: UInt = msgAuth.foldRight(keyRef) { (acc, element) =>
    Cat(acc, element)
  }
  dontTouch(key)
}
class MyBundle extends Bundle {}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
