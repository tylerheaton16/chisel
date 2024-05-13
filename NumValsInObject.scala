//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
import scala.reflect.runtime.universe._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Foo extends Module {
    val (numVals, maxVal) = ObjectStats.getObjectStats(MyObject)
    println(s"Number of vals in MyObject: $numVals")
    println(s"Largest val in MyObject: ${maxVal.getOrElse("N/A")}")

}

object MyObject {
  val a = 0.U
  val b = 3.U
  val c = 6.U
}


object ObjectStats {
  def getObjectStats(obj: Any): (Int, Option[Int]) = {
    val valFields = obj.getClass.getDeclaredFields.filter(_.getType == classOf[UInt])

    val numVals = valFields.length

    val maxVal = valFields.flatMap { field =>
      field.setAccessible(true)
      val uintValue = field.get(obj).asInstanceOf[UInt]
      if (uintValue.getWidth <= 32) {
        Some(uintValue.litValue.toInt)
      } else {
        None
      }
    }.reduceOption(_ max _)

    (numVals, maxVal)
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
