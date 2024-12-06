//> using scala "2.13.14"
//> using dep "org.chipsalliance::chisel:6.4.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.layer.{Convention, Layer, block}

import chisel3.ltl._
import chisel3.ltl.Sequence._
import chisel3.ltl.Property._

// All layers are declared here.  The Assert and Debug layers are nested under
// the Verification layer.
object Verification extends Layer(Convention.Bind) {
  object Assert extends Layer(Convention.Bind)
  object Debug extends Layer(Convention.Bind)
}

import circt.stage.ChiselStage

class Foo extends Module {
  val a = IO(Input(UInt(32.W)))
  val b = IO(Output(UInt(32.W)))

  b := a + 1.U

  block(Verification) {
    val a_d0 = RegNext(a)
    block(Verification.Assert) {
      assert(a >= a_d0, "a must always increment")
      AssertProperty(a(0) === 0.U, label = Some("foo0"))
      // val s0 = Property.eventually(a(0))
      val s0 = Sequence(a(0), Delay(1, 3), !a(1), Delay(5), a(2))
      AssertProperty(s0, label = Some("foo1"))
    }

    block(Verification.Debug) {
      printf("a: %x, a_d0: %x", a, a_d0)
    }
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
