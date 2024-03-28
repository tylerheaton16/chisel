//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  // With Vec, you create 2 elements each 2 bits wide and then must initialize them yourself
  val a = dontTouch { Wire(Vec(2, UInt(2.W))).suggestName("Vec") } // Must initialize
  a(0) := 0.U
  a(1) := 0.U

  // With VecInit, you must pass a Seq[Data] and can attach initialization values to it
  val mySeq = Seq(1.U(3.W), 1.U(1.W))
  val b = dontTouch { VecInit(mySeq).suggestName("VecInit") }
  val z = dontTouch { VecInit.fill(2)(b) }

  // With Wire, you pass it a T <: Data, and then you must tie it off yourself
  val c = dontTouch { Wire(UInt(1.W)).suggestName("Wire") }
  c := 0.U

  // With WireInit, you pass a Data AND a default value to connect it to
  // Notice how in the compiled Verilog it connects d to c (Wire)
  // Also notice that the last connect works so that if C is 1 we are connected to 0, but if false we get connected back
  // to the default which is 1.U
  val d = dontTouch { WireInit(UInt(1.W), 1.U)}
  when (c === 1.U) {d := 0.U}

  // Reg is assigned every clock cycle 
  val e = dontTouch { Reg(UInt(1.W)).suggestName("Reg") }
  e := c

  // RegInit has a reset value now
  val f = dontTouch { RegInit(0.U(10.W)).suggestName("RegInit")}

  // RegNext

  val g = dontTouch { RegNext(f).suggestName("RegNext")}
  val h = dontTouch { Reg(UInt(10.W)).suggestName("RegConnection") }
  h := f

}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
