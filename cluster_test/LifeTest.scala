import com.nicta.scoobi.Scoobi._
import Reduction._
import java.io._

object LifeTest extends ScoobiApp {

    def run() {
    val randomSeeds = fromTextFile(args(0))

    val count = randomSeeds.map(lifeMap(_))
        .groupByKey
        .combine(Sum.int)

    count.toTextFile(args(1)).persist
    }
    def lifeMap(in : String) : (String, Int) = {
        val initialMatrix = padZero(in, 64).split("").drop(1).zipWithIndex.map(w => if(w._1 == "1") (w._2/8, w._2%8) else (16,16)).filter(_ != (16,16)).toList
        var civilization = new Civilization(initialMatrix, 8)

        civilization.printGrid
        for (period <- 1 to 99) {
            civilization = civilization.tick
        }
        val out = civilization.printGrid.drop(1)
        val fw = new FileWriter("res_final.txt", true)
        fw.write(out + "\n")
        fw.close()
        (out, 1)
    }

    def padZero(in : String, size : Int) = {
        var res = in
        while (res.length%size != 0) {
            res = "0" + res
        }
        res
    }

    class Civilization(seed: List[(Int, Int)], size: Int) {
            private val STAY_ALIVE = 2
            private val BE_BORN = 3
            private val grid = Array.ofDim[Boolean](size, size)
            seed foreach (cell => containsCellAt(cell._1, cell._2))

            def tick = {
                val nextGeneration = new Civilization(Nil, size)
                for (row <- 0 until size; col <- 0 until size)
                if (isCellAt((row, col)) && numberOfNeighboursFor(row, col) == STAY_ALIVE
                || numberOfNeighboursFor(row, col) == BE_BORN)
                nextGeneration.containsCellAt(row, col)
                nextGeneration
            }

            private def numberOfNeighboursFor(row: Int, col: Int): Int =
                areaAround(row, col) map wrap filter isCellAt length

            private def areaAround(row: Int, col: Int): List[(Int, Int)] =
                List((row-1, col-1), (row,col-1), (row+1,col-1), (row-1,col), (row+1,col), (row-1,col+1), (row,col+1), (row+1,col+1))

            private def wrap(cell: (Int, Int)): (Int, Int) =
                (((cell._1 + size) % size), ((cell._2 + size) % size))

            private def isCellAt(cell: (Int, Int)): Boolean =
                grid(cell._1)(cell._2)

            private def containsCellAt(row: Int, col: Int) =
                grid(row)(col) = true

            def printGrid = {
                val sb = new StringBuffer
                grid foreach {
                    row => row foreach {
                        cell => if (cell) sb.append("1") else sb.append("0")
                    }
                }
                sb.toString
            }
    }


}
