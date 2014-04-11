package com.usyd.mrsim.comm

import Chisel._

/* Serialise and DeSerialise Int/Long
 * ZigZag
 * See: https://developers.google.com/protocol-buffers/docs/encoding?csw=1
 * n : wether the output will be a 32(n = 1) or 64(n = 2) bit Integer
 * Currently Only takes the entire number and transforms in into a UInt
 */
class encodeZigZagIntBlock(n : Int) extends Module {
  val io = new Bundle {
      val in = SInt(INPUT, width = 32*n)
      val out = UInt(OUTPUT, width = 32*n)
    }
  if (n == 1) {
    io.out := (io.in << UInt(1)) ^ (io.in >> UInt(31))
  } else if (n == 2) {
    io.out := (io.in << UInt(1)) ^ (io.in >> UInt(63))
  }
}

class decodeZigZagIntBlock(n : Int) extends Module {
  val io = new Bundle {
      val in = UInt(INPUT, width = 32*n)
      val out = SInt(OUTPUT, width = 32*n)
    }
    io.out := (io.in >> UInt(1)) ^ (~(io.in & UInt(1)) + UInt(1))
}

/* ZigZag Test Block
 * 
 * Test Each individual Block as well as the two combined
 */
class ZigZagTest(n : Int) extends Module {
  val io = new Bundle {
    //Combined IO
    val inCombined = UInt(INPUT, width = 32*n)
    val outCombined = UInt(OUTPUT, width = 32*n)

    // Decode IO
    val inDecode = UInt(INPUT, width = 32*n)
    val outDecode = UInt(OUTPUT, width = 32*n)
    
    // Encode IO
    val inEncode = UInt(INPUT, width = 32*n)
    val outEncode = UInt(OUTPUT, width = 32*n)
    
    // Combined Modules
    val decodeZigZagIntCombined = Module(new decodeZigZagIntBlock(n))
    val encodeZigZagIntCombined = Module(new encodeZigZagIntBlock(n))

    // Single Modules
    val decodeZigZagInt = Module(new decodeZigZagIntBlock(n))
    val encodeZigZagInt = Module(new encodeZigZagIntBlock(n))
  }
  
  // Combined
  io.encodeZigZagIntCombined.io.in := io.inCombined
  io.decodeZigZagIntCombined.io.in := io.encodeZigZagIntCombined.io.out
  io.outCombined := io.decodeZigZagIntCombined.io.out

  // Decode
  io.decodeZigZagInt.io.in := io.inDecode
  io.outDecode := io.decodeZigZagInt.io.out

  // Encode
  io.encodeZigZagInt.io.in := io.inEncode
  io.outEncode := io.encodeZigZagInt.io.out
}