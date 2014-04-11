package com.usyd.mrsim.comm

import Chisel._

/*
 * Varint Transformation
 * Takes bit stream and search for the complete number
 * c = how any bytes per cycle
 * n = Whether we are doing 32 bit or 64 bit integers.
 * Output needs to route to ZigZag
 * Takes c bytes per cycle in little-endien format
 * Check the most Significatn bit of each byte for continuation
 *
 */
class encodeVarintBlock(n : Int) extends Module {

  private def makeVar(x : UInt, y : UInt) = Mux(y === UInt(0), x, Cat(x,y))

  val io = new Bundle {
      val in = UInt(INPUT, width = 32*n)
      val out = UInt(OUTPUT, width = 40*n)
    }

  val num = Vec.fill(5*n){UInt(width = 8)}
  for (i <- 0 to 4*n) {
  	if (i == 0) {
  		when (io.in >> UInt(7*i) === UInt(0)) {
  			num(i) := UInt(0)
  		} .elsewhen ((io.in >> UInt(7*(i+1)) === UInt(0)) && (io.in >> UInt(7*(i+2)) === UInt(0))){
  			num(i) := io.in >> UInt(7*i)
  		} .otherwise {
  			num(i) := io.in >> UInt(7*i) | UInt(128)
  		}
  	} else {
 		when ((io.in >> UInt(7*i) === UInt(0)) && num(i-1)(7) === UInt(0)) {
  			num(i) := UInt(0)
  		} .elsewhen ((io.in >> UInt(7*(i+1)) === UInt(0)) && (io.in >> UInt(7*(i+2)) === UInt(0))){
  			num(i) := io.in >> UInt(7*i)
  		}.otherwise {
  			num(i) := io.in >> UInt(7*i) | UInt(128)
  		} 		
  	}
  }
  io.out := num.reduceLeft(makeVar)
}

class decodeVarintBlock(n : Int) extends Module {

  private def makeInt(x : UInt, y : UInt) = Mux((y === UInt(0)), x, Cat(x,y(6,0)))

  val io = new Bundle {
      val in = UInt(INPUT, width = 40*n)
      val out = UInt(OUTPUT, width = 32*n)
    }

  val num = Vec.fill(5*n){UInt(width = 8)}

  for (i <- 0 to 4*n) {
  	if(i == 0) {
 		num(i) := ((io.in >> UInt(8*i)) & UInt(127))
  	} else {	
  		num(i) := ((io.in >> UInt(8*i)) & UInt(255))
  	}
}

  io.out := num.reduceLeft(makeInt)

}

/* Varint Test Block
 * 
 * Test Each individual Block as well as the two combined
 */
class VarintTest(n : Int) extends Module {
  val io = new Bundle {
    //Combined IO
    val inCombined = UInt(INPUT, width = 32*n)
    val outCombined = UInt(OUTPUT, width = 32*n)
    
    // Combined Modules
    val decodeVarintCombined = Module(new decodeVarintBlock(n))
    val encodeVarintCombined = Module(new encodeVarintBlock(n))
  }
  
  // Combined
  io.encodeVarintCombined.io.in := io.inCombined
  io.decodeVarintCombined.io.in := io.encodeVarintCombined.io.out
  io.outCombined := io.decodeVarintCombined.io.out
}