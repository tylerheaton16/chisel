//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.5.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage
import chisel3.experimental.BundleLiterals._

class Foo extends Module {

  val oid = VecInit(
    "h06".U(8.W),
    "h09".U(8.W),
    "h60".U(8.W),
    "h86".U(8.W),
    "h48".U(8.W),
    "h01".U(8.W),
    "h65".U(8.W),
    "h03".U(8.W),
    "h04".U(8.W),
    "h02".U(8.W),
    "h08".U(8.W)
  )
  val oidR = oid.reverse
  val a = Wire(UInt(64.W))
  a := oidR(0)

  val g = Cat("h01".U(8.W), "h00".U(8.W), oid.asUInt, 8.U(256.W))
  // val vecOf32BitUInts = VecInit(Seq.tabulate(12)(i => g((i + 1) * 32 - 1, i * 32).asUInt))
  dontTouch(g)

  val size = g.getWidth
// Calculate the number of 32-bit chunks needed (ceiling of 360/32)
  val numChunks = 12 // Result is 12 chunks of 32 bits

// Slice the g into 32-bit chunks and pad the last chunk if necessary
  val vecOf32BitUInts = VecInit(Seq.tabulate(numChunks) { i =>
    if ((i + 1) * 32 <= size) {
      g((i + 1) * 32 - 1, i * 32).asUInt
    } else {
      g(size-1, i * 32).asUInt.pad(32)
    }
  })
  dontTouch(vecOf32BitUInts)

}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info"
        // "--disable-opt",
      )
    )
  )
}
