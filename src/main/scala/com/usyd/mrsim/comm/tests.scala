package com.usyd.mrsim.comm

import Chisel._
import Node._
import scala.collection.mutable.HashMap
import util.Random

class StringBlockTests(c: StringTest) extends Tester(c) {
  for (i <- 0 to 100) {
    val num = Random.nextInt(10000)
    val avroResult = Integer.parseInt(new StringBuilder(num.toHexString.length.toString).append(num.toHexString).toString, 16)
    // Combined Test
    poke(c.io.inCombined, num)

    // Decode Test
    poke(c.io.inEncode, num)

    // Encode Test
    poke(c.io.inDecode, avroResult)

    expect(c.io.outEncode, avroResult)
    expect(c.io.outDecode, num)
    expect(c.io.outCombined, num)

    step(1)
  }
}	

class ZigZagBlockTests(c: ZigZagTest) extends Tester(c) {

  for (i <- 0 to 100) {
    val num = Random.nextInt(10000)
    val avroResult = num*2
    // Combined Test
    poke(c.io.inCombined, num)

    // Decode Test
    poke(c.io.inEncode, num)

    // Encode Test
    poke(c.io.inDecode, avroResult) 

    expect(c.io.outEncode, avroResult)
    expect(c.io.outDecode, num)
    expect(c.io.outCombined, num)

    step(1)
  }
}

class VarintBlockTests(c: VarintTest) extends Tester(c) {
  for (i <- 0 to 100) {
    val num = Random.nextInt(10000)
    poke(c.io.inCombined, num)
    expect(c.io.outCombined, num)
    step(1)
  }
}