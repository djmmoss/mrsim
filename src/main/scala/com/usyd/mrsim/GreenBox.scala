package com.usyd.mrsim.greenbox

import Chisel._
import com.usyd.mrsim.example.life._
import com.usyd.mrsim.util._

import Node._
import scala.collection.mutable.HashMap
import scala.util.Random

class GreenBox[T <: Data, S <: Data](inBundle : T, outBundle : S) extends Module {

    // INPUT & OUTPUT WIRES
    val io = new Bundle {
        val enq_dat = UInt(INPUT, width = 64)
        val enq_val = Bool(INPUT)
        val enq_rdy = Bool(OUTPUT)
        val deq_dat = UInt(OUTPUT, width = 64)
        val deq_val = Bool(OUTPUT)
        val deq_rdy = Bool(INPUT)
    }

    // INPUT & OUTPUT FIFO - SIZE 4
    val in_fifo = Module(new Fifo(UInt(width = 64), 4)).io
    val out_fifo = Module(new Fifo(UInt(width = 64), 4)).io

    // ENCODE AND DECODE MODULES - per-Application
    val data_encode = Module(new encode(outBundle.clone)).io
    val data_decode = Module(new decode(inBundle.clone)).io

    // SINGLE MAPPER
    val map = Module(new Mapper(inBundle, outBundle)).io

    // MAPPER WIRES
    // map.rx_dat - (INPUT)  INPUT DATA
    // map.rx_val - (INPUT)  NEW DATA COMING
    // map.rx_rdy - (OUTPUT) READY FOR NEW DATA
    // map.tx_dat - (OUTPUT) OUTPUT DATA
    // map.tx_val - (OUTPUT) VALID OUTPUT

    // IN --> INFIFO
    in_fifo.enq_dat := io.enq_dat
    in_fifo.enq_val := io.enq_val
    io.enq_rdy := in_fifo.enq_rdy

    // INFIFO --> DECODE
    data_decode.rx_dat := in_fifo.deq_dat
    data_decode.rx_val := in_fifo.deq_val
    in_fifo.deq_rdy := (in_fifo.deq_val && map.rx_rdy)

    // DECODE --> MAP
    map.rx_dat := data_decode.tx_dat
    map.rx_val := data_decode.tx_val

    // MAP --> ENCODE
    data_encode.rx_dat := map.tx_dat
    data_encode.rx_val := map.tx_val

    // ENCODE --> OUTFIFO
    out_fifo.enq_dat := data_encode.tx_dat
    out_fifo.enq_val := data_encode.tx_val

    // OUTFIFO --> OUT
    io.deq_dat := out_fifo.deq_dat
    io.deq_val := out_fifo.deq_val
    out_fifo.deq_rdy := io.deq_rdy
}

class GreenBoxTests(c: GreenBox[inBundle, outBundle]) extends Tester(c) {
    val in = 0x3189544e
    poke(c.io.enq_dat, in)
    poke(c.io.enq_val, 1)
    step(1)
    poke(c.io.enq_val, 0)
    poke(c.io.deq_rdy, 1)
    peek(c.io.deq_dat)
    peek(c.io.deq_val)
    peek(c.map.rx_dat)
    peek(c.map.rx_val)
    peek(c.in_fifo.deq_dat)
    step(1)
    poke(c.io.deq_rdy, 1)
    peek(c.io.deq_dat)
    peek(c.io.deq_val)
    peek(c.map.rx_dat)
    peek(c.map.rx_val)
    peek(c.in_fifo.deq_dat)
    step(1)
    poke(c.io.deq_rdy, 1)
    peek(c.io.deq_dat)
    peek(c.io.deq_val)
    peek(c.map.tx_dat)
    peek(c.out_fifo.enq_dat)
    step(1)
    poke(c.io.deq_rdy, 1)
    peek(c.io.deq_dat)
    peek(c.io.deq_val)
    peek(c.map.tx_dat)
    peek(c.out_fifo.enq_dat)
}

object GreenBox {
    def main(args: Array[String]) : Unit = {
        chiselMainTest(Array("--genHarness", "--test", "--compile"), () => Module(new GreenBox(new inBundle, new outBundle))) {
            c => new GreenBoxTests(c)
        }
    }
}
