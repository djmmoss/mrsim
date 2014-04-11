package com.usyd.mrsim.example

import com.nicta.scoobi.Scoobi._
import Reduction._
import Chisel._
import com.usyd.mrsim._

import scala.util.Random
import scala.sys.process._

object Identity extends ScoobiApp with MrSim {
	def run() {
		val lines = fromTextFile(args(0))

		val counts = points
		  .map(classify)
		  .groupByKey
		  .combine(Reduction.apply(recenter))
		
        counts.toTextFile(args(1)).persist
	}

    def classify(pt: Array[Double]): (Int,(Int, Array[Double])) = {
    val dists = toHardware(w)

    return (bc , (1, pt))
    }


    def recenter(ptl: (Int, Array[Double]), ptr: (Int, Array[Double])) : (Int, Array[Double]) = {
        val nl = ptl._1
        val nr = ptr._1
        val n = nl + nr
        val pt = (ptl._2,ptr._2).zipped map((a,b) => ((nl*a + nr*b)/n))
        val ret = (n, pt)
        return ret
    }


	def IdentityMapper(w : String) : (String, Int) = {
		(toHardware(w), 1)
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
  io.tx_dat("int") := io.rx_dat("int")
  when(io.rx_val) {
    io.tx_val := Bool(true)
  } .otherwise {
    io.tx_val := Bool(false)
  }
}

class Mapper extends Module {

    val io = new Bundle {
        val point = UInt(INPUT, width = n*w)
        val centroid = Vec.fill(c){UInt(INPUT, width = n*w)}
        val out = UInt(OUTPUT, width = n*w)
    }
    val eucDist = Vec.fill(c){Module(new EucDistBlock(n, w)).io}
    val closeCent = Module( new ClosestCentreN(c, w)).io

    for (i <- 0 until c) {
        eucDist(i).point := io.point
        eucDist(i).centroid := io.centroid(i)
        closeCent.in(i) := eucDist(i).out
    }

    io.out := closeCent.out
}

class EucDistBlock(n : Int, w : Int) extends Module {

    private def EucDist(x : UInt, y : UInt) = (x - y) * (x - y)

    private def sumDist(x : UInt, y : UInt) = (x + y)

    val io = new Bundle {
        val point = UInt(INPUT, width = n*w)
        val centroid = UInt(INPUT, width = n*w)
        val out = UInt(OUTPUT, width = w)
    }

    val distSum = Vec.fill(n){UInt(width = w)}

    for (i <- 0 until n) {
        distSum(i) := EucDist(io.point((i+1)*(w-1), i*w), io.centroid((i+1)*(w-1), i*w))
    }
    io.out := distSum.reduceLeft(sumDist)
}

class ClosestCentreN(c : Int, w : Int) extends Module {

    private def Min2(x : UInt, y : UInt) = Mux(x <= y, x, y)

    val io = new Bundle {
        val in = Vec.fill(c){UInt(INPUT, w)}
        val out = UInt(OUTPUT, w)
    }
    io.out := io.in.lastIndexWhere((x : UInt) => {io.in.reduceLeft(Min2) === x})
}


class inBundle() extends Bundle {
	val int = UInt(width = 64)
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

