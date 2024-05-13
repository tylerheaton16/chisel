//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
  val values = Foo.State.all.map { i => i.litValue}
  val names = Foo.State.allNames.map { i => i.toString}
  val pair = names.zip(values)
  //pair.foreach(println(_))
  val tyler = Foo.State()
  println(tyler)
  // mySeq.zipWithIndex.foreach{case(name, value) => println(s"${name} and ${value}")}
  //mySeq.foreach(println(_))


}
object Foo {
  object State extends ChiselEnum {
    val a, b, c = Value
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
