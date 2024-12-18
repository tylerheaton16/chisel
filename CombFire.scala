//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  val resp = IO(Decoupled(new Request))
  val in = IO(Flipped(Decoupled(new Request)))
  val inS = IO(Input(Bool()))
  dontTouch(resp)
  dontTouch(in)
  dontTouch(inS)
  resp.bits <> in.bits
  in.ready := inS && resp.ready

  when(in.fire) {
    resp.valid := true.B
  }.otherwise {
    resp.valid := false.B
  }
  when(resp.fire) {
    resp.bits.data := 5.U(64.W)
  }

}
object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
