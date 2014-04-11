package com.usyd.mrsim.comm

import Chisel._

/* Serialise and DeSerialise String
 * 
 * Encode as long followed by that many bytes f UTF-8
 * example foo --> 06 66 6f 6f
 * No need to encode or decode, just need to read the number of bytes
 * n = expect max string size
 */

class encodeStringBlock(n : Int) extends Module {
  
  private def checkByte(x : UInt) = Mux(x === UInt(0), UInt(0), UInt(1))

  private def sumBytes(x : UInt, y : UInt) = (x + y)

  private def createOut(x : UInt, y : UInt) = Mux(y === UInt(0), x, Cat(x,y))

  val io = new Bundle {
    val in = UInt(INPUT, width = 8*n);
    val out = UInt(OUTPUT, width = (8*n)+ 8)
  }

  val encodeZigZag = Module(new encodeZigZagIntBlock(1))
  val numBytes = Vec.fill(n){UInt(width = 8)}
  val newOut = Vec.fill(n+1){UInt(width = 8)}
  for (i <- 0 until n) {
    numBytes(i) := checkByte(io.in(8*i+7, 8*i))
    newOut(n-i) := io.in(8*i+7, 8*i)
  }
  encodeZigZag.io.in := numBytes.reduceLeft(sumBytes)
  newOut(0) := encodeZigZag.io.out
  io.out := newOut.reduceLeft(createOut)
}


class decodeStringBlock(n : Int) extends Module {
  
  private def makeOut(x : UInt, y : UInt) = Mux(y === UInt(0), x, Cat(y,x))

  val io = new Bundle {
    val in = UInt(INPUT, width = 8*n + 8)
    val out = UInt(OUTPUT, width = 8*n)
  }
  val bytes = Vec.fill(n){UInt(width = 8)}
  for (i <- 1 to n) {
    when ((io.in >> UInt(8*i) & UInt(255)) === UInt(0)) {
      bytes(i-1) := UInt(0) 
    } .otherwise {
      bytes(i-1) := (io.in >> UInt(8*(i-1))) & UInt(255)
    }
  }
  io.out := bytes.reduceLeft(makeOut)
}

/* String Test Block
 * 
 * Test Each individual Block as well as the two combined
 */
class StringTest(n : Int) extends Module {
	val io = new Bundle {
		//Combined IO
		val inCombined = UInt(INPUT, width = 8*n)
		val outCombined = UInt(OUTPUT, width = 8*n)

		// Decode IO
		val inDecode = UInt(INPUT, width = 8*n+8)
		val outDecode = UInt(OUTPUT, width = 8*n)
		
		// Encode IO
		val inEncode = UInt(INPUT, width = 8*n)
		val outEncode = UInt(OUTPUT, width = 8*n+8)
		
		// Combined Modules
		val decodeStringCombined = Module(new decodeStringBlock(n))
		val encodeStringCombined = Module(new encodeStringBlock(n))

		// Single Modules
		val decodeString = Module(new decodeStringBlock(n))
		val encodeString = Module(new encodeStringBlock(n))
	}
	
	// Combined
	io.encodeStringCombined.io.in := io.inCombined
	io.decodeStringCombined.io.in := io.encodeStringCombined.io.out
	io.outCombined := io.decodeStringCombined.io.out

	// Decode
	io.decodeString.io.in := io.inDecode
	io.outDecode := io.decodeString.io.out

	// Encode
	io.encodeString.io.in := io.inEncode
	io.outEncode := io.encodeString.io.out
}