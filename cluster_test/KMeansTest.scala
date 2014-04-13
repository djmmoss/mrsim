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


  /* -- Grab Points from File -- */
  // Need to modify this to accept a arbitrary number of features for each point

 
  def run() {
     val points: DList[String] = fromTextFile(args(0))


      var curCenters : List[Array[Int]] = List()

      
      val bestCmap: DList[(Int, (Int, Array[Int]))] = points
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
    val center = input.drop(1)
    val pt = input(0)
    val dists = center.map(ctr => ((ctr,pt).zipped map((a,b) => (a - b).abs)).sum)

    val bc = dists.indexOf(dists.min ,0)


    return (bc , (1, pt))
  }


  def recenter(ptl: (Int, Array[Int]), ptr: (Int, Array[Int])) : (Int, Array[Int]) = {
    val nl = ptl._1
    val nr = ptr._1
    val n = nl + nr
    val pt = (ptl._2.map(_.toDouble),ptr._2.map(_.toDouble)).zipped map((a,b) => ((nl*a + nr*b)/n))
    val ret = (n, pt.map(_.round.toInt))
    return ret
  }

  def randomPoints(n :Int, feat: Int, max: Float) = {
    for (i <- 0 until n)
    yield Array.fill(feat)(Random.nextDouble()*max)
  }
}
