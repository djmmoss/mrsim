package com.usyd.mrsim.test

/*
 * This Package containes hardware blocks that perform hardware serialization and deserialization
 * of avro schema's using Binary Encoding
 *
 * List of Serialization Types:
 * Boolean - No Block
 * Int and Long - Variable-Length zig-zag codeing
 * String - Long followed by bytes of UTF-8
 * Float - floatToIntBits converts to IEEE 754
 * Double - doubleToIntBits same as float
 * Bytes - Long followed by bytes
 */

import com.usyd.mrsim.comm._
import Chisel._

object AvroTest {

  def main(args: Array[String]) : Unit = {
    chiselMainTest(args,
      () => Module(new StringTest(8))) {
      c => new StringBlockTests(c)
    }
    chiselMainTest(args,
      () => Module(new ZigZagTest(1))) {
      c => new ZigZagBlockTests(c)
    }
    chiselMainTest(args,
      () => Module(new VarintTest(1))) {
      c => new VarintBlockTests(c)
    }
  }
}