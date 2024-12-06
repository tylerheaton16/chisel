//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.5.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage
import chisel3.util.experimental.loadMemoryFromFile
import java.nio.file.Files
import java.nio.file.Paths

class Foo extends Module {

  val ram = Module(new Rom(768, 23, "Crystals23Mem1"))
  ram.io.raddr := 0.U
  ram.io.enable := 1.U
}
class Rom(ramSize: Int, width: Int, name: String) extends Module {
  override def desiredName: String = name

  val io = IO(new TwiddleRomIO(ramSize))

  val fileTRom = SyncReadMem(ramSize, UInt(width.W))

  io.rdData := fileTRom.read(io.raddr, io.enable)
  val chipyardDir = Paths.get(System.getProperty("user.dir"))
  val fileTPath = chipyardDir.resolve(
    "fileT.txt"
  )

  println(s"TYLER: ${fileTPath.toString}")
  if (Files.exists(fileTPath) && Files.size(fileTPath) > 0) {
    loadMemoryFromFile(fileTRom, fileTPath.toString)
  } else {
    throw new Exception("file doesn't exist for Rom")
  }

}
class TwiddleRomIO(ramSize: Int) extends Bundle {
  val raddr = Input(UInt(log2Ceil(ramSize).W))
  val rdData = Output(UInt(64.W))
  val enable = Input(Bool())
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--disable-opt"
      )
    )
  )
}
