//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  val myVec = Reg(Vec(1, UInt(64.W)))
  val cVec = 0.U.asTypeOf(new MyBundle)
  myVec := cVec
  dontTouch(myVec)


}
object Foo {
  //@tailrec
  //def recursiveCount(payLoad: UInt, vec: Vec[UInt], data: UInt, index: UInt): Unit = {
  //  //Seq.tabulate(8)(_ + 1).map(i => vec(i-1) := data)
  //}
  //def recursiveCount(payLoad: UInt, vec: Vec[UInt], data: UInt, index: UInt): Unit = {
  //  if ((index <= 7.U) {
  //    vec(index) := data
  //    recursiveCount(payLoad, vec, data, index + 1.U)
  //  }
  //}
}

class MyBundle extends Bundle {
  val sig1 = UInt(64.W)
  val sig2 = UInt(64.W)
  val sig3 = UInt(64.W)
  val sig4 = UInt(64.W)
  val sig5 = UInt(64.W)
  val sig6 = UInt(64.W)
  val sig7 = UInt(64.W)
  val sig8 = UInt(64.W)
}
object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
