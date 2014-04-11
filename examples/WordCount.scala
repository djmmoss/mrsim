package com.usyd.mrsim.example

import com.nicta.scoobi.Scoobi._
import Reduction._
import Chisel._
import com.usyd.mrsim._

import scala.util.Random
import scala.sys.process._

object WordCount extends ScoobiApp with MrSim {
	def run() {
		val lines = fromTextFile(args(0))

		val counts = lines.mapFlatten(_.split(" ").filter(_.length() > 1))
		  .map(WordMapper(_))
		  .groupByKey
		  .combine(Sum.int)
		counts.toTextFile(args(1)).persist
	}

	def WordMapper(w : String) : (String, Int) = {
        val ret = toHardware(w)
		val res = ret.split("x")(1)
        val count = hexToInt(res.take(2))
        val word = hexToAscii(res.drop(2)).trim
        (word, count)
    }
}
class Mapper[T <: Data, S <: Data](inBundle : T, outBundle : S) extends Module {

    val io = new Bundle {
        val rx_dat = inBundle.clone.asInput
        val rx_val = Bool(INPUT)
        val rx_rdy = Bool(OUTPUT)
        val tx_dat = outBundle.clone.asOutput
        val tx_val = Bool(OUTPUT)
    }

    io.rx_rdy := Bool(true)
    io.tx_dat("int") := Cat(UInt("h01"), io.rx_dat("int"))
    when(io.rx_val) {
        io.tx_val := Bool(true)
    } .otherwise {
        io.tx_val := Bool(false)
    }
}

class inBundle() extends Bundle {
    val int = UInt(width = 56)
    override def clone = { new inBundle().asInstanceOf[this.type]}
}

class outBundle extends Bundle {
    val int = UInt(width = 64)
    override def clone = { new outBundle().asInstanceOf[this.type]}
}

class encode[T <: Data](outBundle : T) extends Module {
    val io = new Bundle {
        val rx_dat = outBundle.clone.asInput
        val rx_rdy = Bool(OUTPUT)
        val rx_val = Bool(INPUT)
        val tx_dat = UInt(OUTPUT, width = 64)
        val tx_val = Bool(OUTPUT)
    }

    io.tx_val := Bool(false)
    io.rx_rdy := Bool(true)
    io.tx_dat := io.rx_dat("int")
    when (io.rx_val) {
        io.tx_val := Bool(true)
    }
}

class decode[T <: Data](inBundle : T) extends Module {
    val io = new Bundle {
        val rx_dat = UInt(INPUT, width = 64)
        val rx_rdy = Bool(OUTPUT)
        val rx_val = Bool(INPUT)
        val tx_dat = inBundle.clone.asOutput
        val tx_val = Bool(OUTPUT)
    }

    io.tx_val := Bool(false)
    io.rx_rdy := Bool(true)
    io.tx_dat("int") := io.rx_dat
    when (io.rx_val) {
        io.tx_val := Bool(true)
    }
}


