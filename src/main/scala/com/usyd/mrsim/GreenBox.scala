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
    val in_fifo = Module(new Fifo(inBundle.clone, 4)).io
    val out_fifo = Module(new Fifo(outBundle.clone, 4)).io

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

    // IN --> DECODE
    data_decode.rx_dat := io.enq_dat
    data_decode.rx_val := io.enq_val

    // INFIFO ENQ_READY -- IF DECODE AND FIFO ARE READY
    io.enq_rdy := data_decode.rx_rdy && in_fifo.enq_rdy

    // DECODE --> INFIFO
    in_fifo.enq_dat := data_decode.tx_dat
    in_fifo.enq_val := data_decode.tx_val

    // INFIFO DEQ_VAL -- IF MAPPER IS READY
    in_fifo.deq_rdy := map.rx_rdy

    // INFIFO --> MAP
    map.rx_dat := in_fifo.deq_dat
    map.rx_val := in_fifo.deq_val && map.rx_rdy

    // MAP --> OUTFIFO
    out_fifo.enq_dat := map.tx_dat
    out_fifo.enq_val := map.tx_val

    // OUTFIFO --> ENCODE
    data_encode.rx_dat := out_fifo.deq_dat
    data_encode.rx_val := out_fifo.deq_val

    // OUTFIFO DEQ_VAL -- IF READY TO TAKE OUTPUT
    out_fifo.deq_rdy := io.deq_rdy

    // ENCODE --> OUT
    io.deq_dat := data_encode.tx_dat
    io.deq_val := data_encode.tx_val
}

class GreenBoxTests(c: GreenBox[inBundle, outBundle]) extends Tester(c) {
    val in = 0x3189544e
    poke(c.io.enq_dat, in)
    poke(c.io.enq_val, 1)
    poke(c.io.deq_rdy, 1)
    step(1)
    poke(c.io.enq_val, 0)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    poke(c.io.enq_val, 1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    poke(c.io.enq_val, 1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    poke(c.io.enq_val, 0)
    expect(c.io.deq_dat, 865820326989202446L)
    expect(c.io.deq_val, 1)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 865820326989202446L)
    expect(c.io.deq_val, 1)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 0)
    expect(c.io.deq_val, 0)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
    step(1)
    expect(c.io.deq_dat, 865820326989202446L)
    expect(c.io.deq_val, 1)
    peek(c.in_fifo.deq_dat)
    peek(c.in_fifo.deq_val)
    peek(c.map.rx_rdy)
}

object GreenBox {
    def main(args: Array[String]) : Unit = {
        chiselMainTest(args, () => Module(new GreenBox(new inBundle, new outBundle))) {
            c => new GreenBoxTests(c)
        }
    }
}
