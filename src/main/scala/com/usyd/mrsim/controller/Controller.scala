package com.usyd.mrsim.controller

import Chisel._
import com.usyd.mrsim.comm._
import com.usyd.mrsim.util._
import com.usyd.mrsim.example._

import Node._
import scala.collection.mutable.HashMap
import scala.util.Random

/**
 * Controller
 * 
 * This controller is responsible to taking the bundle input from the IOBlock.
 * The controller distribues the data to a free FIFO which is linked to a mapper.
 * Once the mapper requires input it deqs the data from the FIFO and routes it to
 * the mapper. When a mapper finishes its computation it signals the controller
 * which enqs the result to an output FIFO. The controller then selects a FIFO and
 * deqs its data to output back to the IOBlock.
 * 		 			
 *
 * @param	inBundle 	$inBundle			Input IO for the Mappers
 * @param	outBundle 	$outBundle 		Output IO for the Mappers
 * @param	Int 			$n_int 			Number of Mappers/FIFO
 * @param	Int 			$n_lng			Amount of Memory for each FIFO
 */

class Controller[T <: Data, S <: Data](
	inBundle : T, 
	outBundle : S,  
	n : Int, 
	m: Int
	) extends Module {

	val io = new Bundle {
		val rx_dat = inBundle.clone.asInput
		val rx_val = Bool(INPUT)
		val tx_dat = outBundle.clone.asOutput
		val tx_val = Bool(OUTPUT)
	}

    io.tx_dat("int") := UInt(0)
	io.tx_val := Bool(false)

	// COUNTERS
	val inCounter = Reg(init = UInt(0, UInt(n).getWidth))

	// INPUT FIFO
	val f_in = Vec.fill(n){Module(new Fifo(inBundle, m)).io}

	// OUTPUT FIFO
	val f_out = Vec.fill(n){Module(new Fifo(outBundle, m)).io}

	// MAPPERS
	val map = Vec.fill(n){Module(new Mapper(inBundle, outBundle)).io}

	val f_out_deq_rdy = Vec.fill(n){UInt(width = 1)}
	for (i <- 0 until n) {
		f_out_deq_rdy(i) := f_out(i).deq_val.toBits().toUInt()
	}

	val index : UInt = f_out_deq_rdy.lastIndexWhere((a : UInt) => (a === UInt(1)))


	for (i <- 0 until n) {
		// SET INPUT TO FIFO
		f_in(i).enq_dat := io.rx_dat
		when (UInt(i) === inCounter && io.rx_val && f_in(i).enq_rdy) {
			f_in(i).enq_val := Bool(true)
			inCounter := Mux(inCounter === UInt(n-1), UInt(0), inCounter + UInt(1))
		} .otherwise {
			f_in(i).enq_val := Bool(false)
		}

		// LOAD MAP INPUT WITH FIFO VALUE
		map(i).rx_dat := f_in(i).deq_dat
		when (map(i).rx_rdy && f_in(i).deq_val) {
			f_in(i).deq_rdy := Bool(true) 
			map(i).rx_val := Bool(true)
		} .otherwise {
			f_in(i).deq_rdy := Bool(false)
			map(i).rx_val := Bool(false)
		}
	
		// LOAD MAP OUTPUT TO FIFO
		f_out(i).enq_dat := map(i).tx_dat
		when (map(i).tx_val && f_out(i).enq_rdy) {
			f_out(i).enq_val := Bool(true)
		} .otherwise {
			f_out(i).enq_val := Bool(false)
		}

		
		// OUTPUT FROM FIFO TO IOBLOCK
		when (UInt(i) === index && (f_out(i).deq_val)){
			f_out(i).deq_rdy := Bool(true)
			io.tx_dat := f_out(i).deq_dat
			io.tx_val := Bool(true)
		} .otherwise {
			f_out(i).deq_rdy := Bool(false)
		}
	}
}


class ControllerTests(c : Controller[inBundle, outBundle]) extends Tester(c) {
	for (i <- 1 until 105) {
		val id = Random.nextInt(1000)
		if (i < 100) {
			val num = i
			poke(c.io.rx_dat.int, num) 
			poke(c.io.rx_val, 1)
			peek(c.io.tx_dat.int)
		} else {
			val num = 0
			poke(c.io.rx_dat.int, num)
			poke(c.io.rx_val, 1)
		}
		step(1)
	}
}

object Controller {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args,
      () => Module(new Controller(	new inBundle(), 
      								new outBundle(), 
      								2, 
      								2))) {
    	c => new ControllerTests(c)
    }
  }
}
