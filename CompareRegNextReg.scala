//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  // RegInit has a reset value now
  val read = IO(new Read)
  val readReg = RegInit(0.U.asTypeOf(read.isRead))
  readReg := read.isRead

  //val compare = RegNext(read.isRead) =/= readReg
  val compare = (read.isRead) =/= readReg
  dontTouch(read)
  dontTouch(readReg)
  dontTouch(compare)
}
class Read extends Bundle {
  val isRead = Input(Bool())
}
class Write extends Bundle {
  val isWrite = Input(Bool())
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
