//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  val mem = SyncReadMem(4096, UInt(64.W))
  val rdAddr = RegInit(0.U(64.W))
  val wire = WireDefault(0.U(64.W))
  val enable = WireDefault(0.U(1.U))

  val cnt = Counter(8)
  val memIn     = RegInit(VecInit(Seq.fill(3)(0.U(64.W))))
  (0 until memIn.size).foreach { i =>
    memIn(i) := 4.U

  }
  dontTouch(memIn)
  println(s"memIn is: $memIn.size")

  mem.write(0.U, 10.U)
  when(cnt.value >= 3.U) {
    cnt.reset()
  }.otherwise {
    rdAddr := cnt.value
    wire := mem.read(rdAddr, enable)
    cnt.inc()

  }

  dontTouch(wire)
  dontTouch(rdAddr)
  dontTouch(enable)
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
