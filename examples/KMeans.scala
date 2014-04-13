package com.usyd.mrsim.example

import com.nicta.scoobi.Scoobi._
import Reduction._
import Chisel._
import com.usyd.mrsim._

import scala.util.Random
import scala.sys.process._


object KMeans extends ScoobiApp with MrSim {

    def run() {
        val thePoints: DList[String] = fromTextFile(args(0))


        var curCenters : List[Array[Int]] = List()


        val bestCmap: DList[(Int, (Int, Array[Int]))] = thePoints
        .map(classify)
        .groupByKey
        .combine(Reduction.apply(recenter))

        bestCmap.persist.run.foreach(a => curCenters :+= a._2._2)



        print("\nK-Means Finished")
        print("\n\tCenters:\t")
        curCenters.foreach(a => {print("\n\t\t\t")
        a.foreach(b => print(b.toString + "\t"))})

    }



    def classify(in: String): (Int,(Int, Array[Int])) = {
        val input = in.split(":").map(_.split(",").map(_.trim.toInt))
        val in_h = input.map(_.map(a => padZero(a.toHexString, 2)).mkString).mkString
        val dists = toHardware(true, in_h)
        val raw = dists.split("x")(1).drop(8)
        val pt2 = raw.take(2)
        val pt1 = raw.drop(2).take(2)
        val bc = raw.drop(4).take(2).toInt
        val pt_h = Array(Integer.parseInt(pt1, 16), Integer.parseInt(pt2, 16))
        return (bc , (1, pt_h))
    }

    def recenter(ptl: (Int, Array[Int]), ptr: (Int, Array[Int])) : (Int, Array[Int]) = {
        val nl = ptl._1
        val nr = ptr._1
        val n = nl + nr
        val pt = (ptl._2.map(_.toDouble),ptr._2.map(_.toDouble)).zipped map((a,b) => ((nl*a + nr*b)/n))
        val ret = (n, pt.map(_.round.toInt))
        return ret
    }

}




class Mapper[T <: Data, S <: Data](inBundle : T, outBundle : S) extends Module {

    val c = 3
    val n = 2
    val w = 8

    val io = new Bundle {
        val rx_dat = inBundle.clone.asInput
        val rx_val = Bool(INPUT)
        val rx_rdy = Bool(OUTPUT)
        val tx_dat = outBundle.clone.asOutput
        val tx_val = Bool(OUTPUT)
    }

    val eucDist = Vec.fill(c){Module(new EucDistBlock(n, w)).io}
    val closeCent = Module( new ClosestCentreN(c, w)).io

    eucDist(0).point(0) := io.rx_dat("int")
    eucDist(1).point(0) := io.rx_dat("int")
    eucDist(2).point(0) := io.rx_dat("int")

    eucDist(0).point(1) := io.rx_dat("int1")
    eucDist(1).point(1) := io.rx_dat("int1")
    eucDist(2).point(1) := io.rx_dat("int1")

    eucDist(0).centroid(0) := io.rx_dat("c11")
    eucDist(0).centroid(1) := io.rx_dat("c12")
    closeCent.in(0) := eucDist(0).out

    eucDist(1).centroid(0) := io.rx_dat("c21")
    eucDist(1).centroid(1) := io.rx_dat("c22")
    closeCent.in(1) := eucDist(1).out

    eucDist(2).centroid(0) := io.rx_dat("c31")
    eucDist(2).centroid(1) := io.rx_dat("c32")
    closeCent.in(2) := eucDist(2).out

    io.rx_rdy := Bool(true)
    io.tx_dat("int") := io.rx_dat("int")
    io.tx_dat("int1") := io.rx_dat("int1")
    io.tx_dat("cent") := closeCent.out

    when(io.rx_val) {
        io.tx_val := Bool(true)
    } .otherwise {
        io.tx_val := Bool(false)
    }
}

class EucDistBlock(n : Int, w : Int) extends Module {

    private def EucDist(x : UInt, y : UInt) = (x - y) * (x - y)

    private def sumDist(x : UInt, y : UInt) = (x + y)

    val io = new Bundle {
        val point = Vec.fill(n){UInt(INPUT, width = 64)}
        val centroid = Vec.fill(n){UInt(INPUT, width = 64)}
        val out = UInt(OUTPUT, width = 64)
    }

    val distSum = Vec.fill(n){UInt(width = 64)}

    for (i <- 0 until n) {
        distSum(i) := EucDist(io.centroid(i), io.point(i))
    }
    io.out := distSum.reduceLeft(sumDist)
}

class ClosestCentreN(c : Int, w : Int) extends Module {

    private def Min2(x : UInt, y : UInt) = Mux(x <= y, x, y)

    val io = new Bundle {
        val in = Vec.fill(c){UInt(INPUT, 64)}
        val out = UInt(OUTPUT, 64)
    }
    io.out := io.in.lastIndexWhere((x : UInt) => {io.in.reduceLeft(Min2) === x})
}

class inBundle() extends Bundle {
	val int = UInt(width = 8)
    val int1 = UInt(width = 8)
    val c11 = UInt(width = 8)
    val c12 = UInt(width = 8)
    val c21 = UInt(width = 8)
    val c22 = UInt(width = 8)
    val c31 = UInt(width = 8)
    val c32 = UInt(width = 8)
  override def clone = { new inBundle().asInstanceOf[this.type]}
}

class outBundle extends Bundle {
	val int = UInt(width = 8)
    val int1 = UInt(width = 8)
    val cent = UInt(width = 8)
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
  io.tx_dat := Cat(io.rx_dat("int1"), io.rx_dat("int"), io.rx_dat("cent"), UInt("hff"))
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
  io.tx_dat("c11") := io.rx_dat(7,0)
  io.tx_dat("c12") := io.rx_dat(15,8)
  io.tx_dat("c21") := io.rx_dat(23,16)
  io.tx_dat("c22") := io.rx_dat(31, 24)
  io.tx_dat("c31") := io.rx_dat(39,32)
  io.tx_dat("c32") := io.rx_dat(47,40)
  io.tx_dat("int") := io.rx_dat(55, 48)
  io.tx_dat("int1") := io.rx_dat(63, 56)
  when (io.rx_val) {
    io.tx_val := Bool(true)
  }
}

