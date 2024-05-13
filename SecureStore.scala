//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel::6.2.0"
//> using plugin "org.chipsalliance:::chisel-plugin::6.2.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

  class Foo extends Module {
    val nextState = Wire(State())
    val stateReg = RegInit(State.idle)
    val secureStoreIO = IO(Flipped(new SecureStoreIO()))
    nextState := stateReg
    stateReg := nextState
    secureStoreIO.enable := (stateReg === State.enrGetAuth) || (stateReg === State.enrWriteToken)
    secureStoreIO.write  := (stateReg === State.enrWriteToken)
    secureStoreIO.addr   := 0.U
    secureStoreIO.wrData := 0.U

    val cnt         = RegInit(0.U(3.W))
    val (data) = SecureStore.read(nextState, State.enrCheckAuth, SecureStore.enrAuthOffset, cnt, cntTo = 2.U, secureStoreIO)
    dontTouch(data)

  }
 class SecureStoreIO extends Bundle {
  val addr = Input(UInt(8.W))

  val rdData = Output(UInt((64).W))
  val wrData = Input(UInt((64).W))

  val enable = Input(Bool())
  val write  = Input(Bool())
}
 object SecureStore {
  val enrAuthOffset = 0.U
  val audAuthOffset = 3.U

  def read(nextState: State.Type, next: State.Type, offset: UInt, cnt: UInt, cntTo: UInt, ss: SecureStoreIO) = {
    val data = Wire(UInt(ss.rdData.getWidth.W))

    ss.addr := cnt + offset
    data    := ss.rdData

    when(cnt >= cntTo) {
      nextState := next
      cnt       := 0.U
    }.otherwise {
      cnt := cnt + 1.U
    }
    data
  }
}
object State extends ChiselEnum {
 val idle          = Value
 val constructData = Value

 /* Response States */
 val ack            = Value
 val reject         = Value
 val parsePrimitive = Value

 /* Secure Stores States */
 val readSecureStore  = Value
 val writeSecureStore = Value
 /* PC.Authenticate(C, idp, idv) ──► T */

 // Enroller States
 val enrCheckState = Value
 val enrGetAuth    = Value
 val enrCheckAuth  = Value
 val enrGenToken   = Value
 val enrWriteToken = Value
 val waitEnrRead   = Value
 val gotEnrRead    = Value
 val enrResp       = Value

 // Auditor States
 val audCheckState = Value
 val audCheckAuth  = Value
 val audGenToken   = Value
 val audWriteToken = Value
 val waitAudRead   = Value
 val gotAudRead    = Value
 val audResp       = Value
}
object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new Foo,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
