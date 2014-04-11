package com.usyd.mrsim.sim

import Chisel._
import com.usyd.mrsim._
import com.usyd.mrsim.controller._
import com.usyd.mrsim.util._
import com.usyd.mrsim.comm._
import com.usyd.mrsim.example._

import Node._
import scala.collection.mutable.HashMap
import scala.util.Random


class MrSimSimulation[T <: Data, S <: Data](inBundle : T,
	outBundle: S,
	fifoSizeCtl : Int,
	numMapper : Int,
	m : Int) extends Module {

	val io = new Bundle {
		val enq_dat = UInt(INPUT, width = 64)
		val enq_val = Bool(INPUT)
		val enq_rdy = Bool(OUTPUT)
		val deq_dat = UInt(OUTPUT, width = 64)
		val deq_val = Bool(OUTPUT)
		val deq_rdy = Bool(INPUT)
	}

	// INPUT & OUTPUT FIFO
	val in_fifo = Module(new Fifo(UInt(width = 64), m)).io
	val out_fifo = Module(new Fifo(UInt(width = 64), m)).io

	val data_encode = Module(new encode(outBundle.clone)).io
	val data_decode = Module(new decode(inBundle.clone)).io

	// New Controller
	val crtler = Module(new Controller(inBundle.clone,
		outBundle.clone,
		numMapper,
		fifoSizeCtl)).io

	// Input into fifo
	in_fifo.enq_dat := io.enq_dat
	in_fifo.enq_val := io.enq_val
    io.enq_rdy := in_fifo.enq_rdy

	// Decode from FIFO
	data_decode.rx_dat := in_fifo.deq_dat
	data_decode.rx_val := (in_fifo.deq_val && data_decode.rx_rdy)
	in_fifo.deq_rdy := (data_decode.rx_rdy && in_fifo.deq_val)

	// Encode
	data_encode.rx_dat := crtler.tx_dat
	data_encode.rx_val := crtler.tx_val

	// Input controller from encode
	crtler.rx_dat := data_decode.tx_dat
	crtler.rx_val := data_decode.tx_val

	//Output Controller to Fifo
	out_fifo.enq_dat := data_encode.tx_dat
	out_fifo.enq_val := data_encode.tx_val

	// Output from Fifo
	io.deq_dat := out_fifo.deq_dat
	io.deq_val := out_fifo.deq_val
	out_fifo.deq_rdy := io.deq_rdy

}

class MrSimSimulationTests(c : MrSimSimulation[inBundle, outBundle]) extends Tester(c) {
	for (i <- 1 until 1000) {
		val num = i
		val id = Random.nextInt()
		poke(c.io.enq_dat, num)
		poke(c.io.enq_val, 1)
		poke(c.io.deq_rdy, 1)
		peek(c.io.deq_dat)
		peek(c.io.deq_val)
		step(1)
	}
}

object MrSimSimulation extends MrSim {
	// Main
	def main(args: Array[String]) : Unit = {
		chiselMainTest(args,
			() => Module(new MrSimSimulation(new inBundle(),
				new outBundle(),
				ctlFifo,
				numMap,
				simFifo))) {
			c => new MrSimSimulationTests(c)
		}
	}
}
