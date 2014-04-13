package com.usyd.mrsim.example

import com.nicta.scoobi.Scoobi._
import Reduction._
import Chisel._
import com.usyd.mrsim._

import scala.util.Random
import scala.sys.process._


object KMeans extends ScoobiApp with MrSim {

    var curCenters: List[Array[Int]] = List(Array(43, 235), Array(154, 4), Array(156, 105))

    var cost = 0.0d

    /* -- Grab Points from File -- */
    // Need to modify this to accept a arbitrary number of features for each point


    def run() {
        val points: DList[String] = fromTextFile(args(0))

        var prevCost = 0.0d
        val epslion = 0.0001
        var count = 0
        var finish = true
        var costList :List[Double] = List()

        while (finish) {

            var tempCenters : List[Array[Int]] = List()

            prevCost = cost
            cost = 0.0d

            val bestCmap: DList[(Int, (Int, Array[Int]))] = points
            .map(classify)
            .groupByKey
            .combine(Reduction.apply(recenter))

            bestCmap.persist.run.foreach(a => tempCenters :+= a._2._2)

            curCenters = tempCenters

            costList:+= cost

            count = count + 1
            if ((prevCost - cost).abs < epslion && count > 1)
                finish = false
        }

        print("\nK-Means Finished with:" + "\n\tCost:\t\t" + "%4.2f".format(cost))
        print("\n\tCostList: \t")
        costList.foreach(a => print("%4.2f".format(a) + "\t"))
        print("\n\tCenters:\t")
        curCenters.foreach(a => {print("\n\t\t\t")
        a.foreach(b => print(b.toString + "\t"))})
        print("\n\tIterations:\t" + count + "\n")

    }
    def classify(in: String): (Int,(Int, Array[Int])) = {
        val pt : Array[Int] = in.split(",").map(w => w.trim.toInt)
        val w = curCenters++Array(pt)
        val in_h = w.map(_.map(a => padZero(a.toHexString, 2)).mkString).mkString
        println(in_h)
        val dists = toHardware(true, in_h)
        println(dists)
        val raw_pt = dists.split("x")(1).drop(8).drop(4)
        println(raw_pt)
        val pt_h = Array(Integer.parseInt(raw_pt.take(2), 16), Integer.parseInt(raw_pt.drop(2), 16))
        val bc = raw_pt.drop(4).take(4).toInt
        println(bc)
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

    eucDist(0).point := io.rx_dat("int")
    eucDist(1).point := io.rx_dat("int")
    eucDist(2).point := io.rx_dat("int")

    eucDist(0).centroid := io.rx_dat("c1")
    closeCent.in(0) := eucDist(0).out

    eucDist(1).centroid := io.rx_dat("c2")
    closeCent.in(1) := eucDist(1).out

    eucDist(2).centroid := io.rx_dat("c3")
    closeCent.in(2) := eucDist(2).out

    io.rx_rdy := Bool(true)
    io.tx_dat("int") := io.rx_dat("int")
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
	val int = UInt(width = 16)
    val c1 = UInt(width = 16)
    val c2 = UInt(width = 16)
    val c3 = UInt(width = 16)
  override def clone = { new inBundle().asInstanceOf[this.type]}
}

class outBundle extends Bundle {
	val int = UInt(width = 16)
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
  io.tx_dat := Cat(io.rx_dat("cent"), UInt("h0000"), io.rx_dat("int"))
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
  io.tx_dat("int") := io.rx_dat(15,0)
  io.tx_dat("c1") := io.rx_dat(31,16)
  io.tx_dat("c2") := io.rx_dat(47,32)
  io.tx_dat("c3") := io.rx_dat(63,48)
  when (io.rx_val) {
    io.tx_val := Bool(true)
  }
}

