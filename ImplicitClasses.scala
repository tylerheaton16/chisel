//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage
import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import chisel3.util.experimental.BoringUtils

class Foo extends Module {
  import Foo._

  val test = WireDefault(0.U(32.W))
  test := "h1C76_B5F4".U
  val testl = test.toLittleEndian
  dontTouch(test)
  dontTouch(testl)

  val byteVec = VecInit(Seq.tabulate(4) { i =>
    test((i + 1) * 8 - 1, i * 8)
  })
  // F4B5761C
  dontTouch(byteVec)
  val e = (byteVec).asUInt
  dontTouch(e)

}
object Foo {

  implicit class UIntLittleEndian(val u: UInt) {
    def toLittleEndian: UInt = {
      val width = u.getWidth
      require(
        width % 8 == 0,
        "UInt width must be a multiple of 8 to convert to little-endian"
      )

      val bytes = width / 8
      val byteVec = VecInit(Seq.tabulate(bytes) { i =>
        u((i + 1) * 8 - 1, i * 8)
      })
      VecInit(byteVec.reverse).asUInt
    }
  }
  implicit class VecUIntLittleEndian(val v: Vec[UInt]) {
    def toLittleEndian: Vec[UInt] = {
      VecInit(v.map(_.toLittleEndian))
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
