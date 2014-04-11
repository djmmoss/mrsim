package com.usyd.mrsim.example

import com.nicta.scoobi.Scoobi._
import Reduction._
import Chisel._
import com.usyd.mrsim._

import scala.util.Random
import scala.sys.process._

object Life extends ScoobiApp with MrSim {
	def run() {
		val lines = fromTextFile(args(0))

		val counts = lines
            .map(w => LifeMapper(w.trim.toInt))
		  .groupByKey
		  .combine(Sum.int)
		counts.toTextFile(args(1)).persist
	}

	def LifeMapper(w : Int) : (String, Int) = {
        (toHardware(Int.box(w)), 1)
	}
}
 
class Cell extends Module {
val io = new Bundle {
    val in = Bool(INPUT)
    val in_val = Bool(INPUT)
    val nbrs = Vec.fill(9){ Bool(INPUT) }
    val out  = Bool(OUTPUT)
  }
  val isAlive = Reg(Bool())
  val count   = io.nbrs.foldRight(UInt(0, 3))((x: Bool, y: UInt) => x.toUInt + y)
  when (io.in_val) {
    isAlive := io.in
  } .otherwise {
    when (isAlive && count < UInt(2)) {
      isAlive := Bool(false)
    } .elsewhen (isAlive && count < UInt(4)) {
      isAlive := Bool(true)
    } .elsewhen (isAlive && count >= UInt(4)) {
      isAlive := Bool(false)
    } .elsewhen(!isAlive && count === UInt(3)) {
      isAlive := Bool(true)
    }
  }
  io.out := isAlive
}

class Mapper[T <: Data, S <: Data](inBundle : T, outBundle : S) extends Module {
  val n = 8
    val tot = n*n
    val size = n
    def counter(max: UInt) = {
    val x = Reg(init = UInt(0, max.getWidth))
    x := Mux(x === max, UInt(0), x + UInt(1))
    x
  }

    def numberOfNeighboursFor(row: Int, col: Int): List[(Int, Int)] =
            areaAround(row, col) map wrap

    def areaAround(row: Int, col: Int): List[(Int, Int)] =
            List((row-1, col-1), (row,col-1), (row+1,col-1), (row-1,col),(row, col), (row+1,col), (row-1,col
                +1), (row,col+1), (row+1,col+1))

    def wrap(cell: (Int, Int)): (Int, Int) =
            (((cell._1 + size) % size), ((cell._2 + size) % size))

  def idx(i: Int, j: Int) = j*n + i

  def nbrIdx(di: Int, dj: Int) = (dj+1)*3 + (di+1)

   val io = new Bundle {
    val rx_dat = inBundle.clone.asInput
    val rx_val = Bool(INPUT)
    val rx_rdy = Bool(OUTPUT)
    val tx_dat = outBundle.clone.asOutput
    val tx_val = Bool(OUTPUT)
  }

  val is_full = Reg(init = Bool(false))
  val check_out = Reg(init = UInt(1, width = tot))
  val in = UInt(width = tot)
  in := io.rx_dat("int")
  val cells = Range(0, tot).map(i => Module(new Cell()))

  for (i <- 0 until tot) {
    when (io.rx_val) {
      cells(i).io.in_val := Bool(true)
      cells(i).io.in := in(i).toBool()
      is_full := Bool(true)
    } .otherwise {
      cells(i).io.in_val := Bool(false)
      cells(i).io.in := Bool(false)
    }
  }

  val out = Vec.fill(tot){Bool()}
  for (k <- 0 until tot){
    out(k) := cells(k).io.out
  }
  for (j <- 0 until n) {
    for (i <- 0 until n) {
      val cell = cells(j*n + i)
        val mapCells = numberOfNeighboursFor(j,i).map(w => w._1 + w._2*8).zipWithIndex
        mapCells.foreach(c => if(c._2 != 4) cell.io.nbrs(c._2) := cells(c._1).io.out else cell.io.nbrs(c._2) := Bool(false))
    }
  }

  io.rx_rdy := !is_full

  when(counter(UInt(99)) === UInt(99)) {
    io.tx_dat("int") := check_out
    check_out := UInt(0)
    io.tx_val := Bool(true)
    is_full := Bool(false)
  } .otherwise {
    check_out := out.toBits().toUInt()
    io.tx_dat("int") := check_out
    io.tx_val := Bool(false)
  }
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
