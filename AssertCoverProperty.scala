//> using scala "2.13.12"
//> using repository "sonatype-s01:snapshots"
//> using dep "org.chipsalliance::chisel::7.0.0-M1+74-3c558514-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin::7.0.0-M1+74-3c558514-SNAPSHOT"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ymacro-annotations"

import chisel3._
import circt.stage.ChiselStage

import chisel3.ltl._
import chisel3.ltl.Sequence._
import chisel3.ltl.Property._

class Foo extends Module {
  val a, b, c, d, e = IO(Input(Bool()))

  AssertProperty(a |=> b ### c)
  CoverProperty(d |-> eventually(e), label = Some("cool_prop"))
}

object Main extends App {
  println(ChiselStage.emitCHIRRTL(new Foo))
  println(
    ChiselStage.emitSystemVerilogFile(
      new Foo,
      firtoolOpts = Array("--strip-debug-info")
    )
  )
}
