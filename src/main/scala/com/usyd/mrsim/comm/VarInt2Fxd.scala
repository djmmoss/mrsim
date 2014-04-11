package com.usyd.mrsim.comm

import Chisel._

class VarInt2Fxd(bo:Int) extends Module {
  val bi:Int = bo+((bo-1)/8+1)
  val io = new Bundle {
    val in = UInt(INPUT, 8*bi)
    val out = UInt(OUTPUT, 8*bo)
    val bcnt = UInt(OUTPUT, 4)
  }

  val num = Vec.fill(bi){ UInt(width = 7) }
  val msb = Vec.fill(bi){ UInt(width = 1) }
  val imsb = Vec.fill(bi+1){ Bool() }
  val cnum = Vec.fill(bi){ UInt(width = 8*bo) }

  val mout = UInt(width = 8*bo)
  val bcnt = UInt(width = 4)

  for (i <- 0 until bi) {
    num(i) := io.in(8*i+6, 8*i)
    msb(i) := io.in(8*i+7)
    imsb(i) := ~(msb(i).toBool())
  }
  imsb(bi) := Bool(true)

  //build all concat options
  cnum(0) := num(0)
  for (i <- 1 until bi) { cnum(i) := Cat(num(i), cnum(i-1)(7*i-1, 0)) }

  val pos1 = PriorityEncoder(imsb)
  when (pos1 === UInt(bi)) { 
    bcnt := UInt(0) 
    mout := UInt(0)
  } .otherwise { 
    bcnt := pos1 
    mout := cnum(pos1)
  }

  io.out := mout
  io.bcnt := bcnt
}
