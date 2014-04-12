/**
  * K-Means Clustering Algorithm
  *
  * This generates a number of points and centers.
  * Then it peforms one iteration of K-Means and calculates the cost
  * The algorithm will continue to run until the cost converges.
  * 
  * Changed from tuples to array based points
  **/

import com.nicta.scoobi.Scoobi._
import Reduction._
import scala.concurrent._
import scala.util._

object KMeansTest extends ScoobiApp {

  var curCenters: List[Array[Int]] = List(Array(43, 235), Array(154, 4), Array(156, 105))

  def run() {
    val points: DList[String] = fromTextFile(args(0))

    var tempCenters : List[Array[Int]] = List()

    val bestCmap: DList[(Int, (Int, Array[Int]))] = points
    .map(classify)
    .groupByKey
    .combine(Reduction.apply(recenter))

    bestCmap.persist.run.foreach(a => tempCenters :+= a._2._2)


    print("\nSingle Iteration K-Means Finished")
    print("\n\tCenters:\t")
    tempCenters.foreach(a => {print("\n\t\t\t")
    a.foreach(b => print(b.toString + "\t"))})

  }

  def classify(in: String): (Int,(Int, Array[Int])) = {
    val pt : Array[Int] = in.split(",").map(w => w.trim.toInt)
    val dists = curCenters.map(ctr => ((ctr,pt).zipped map((a,b) => (a - b).abs)).sum)    
    val bc = dists.indexOf(dists.min ,0)

    return (bc , (1, pt))
  }


  def recenter(ptl: (Int, Array[Int]), ptr: (Int, Array[Int])) : (Int, Array[Int]) = {
    val nl = ptl._1
    val nr = ptr._1
    val n = nl + nr
    
    val pt = (ptl._2,ptr._2).zipped map((a,b) => ((nl*a + nr*b)/n))
    val ret = (n, pt)
    pt.foreach(a => print(a + ", "))
    println
    return ret
  }

}
