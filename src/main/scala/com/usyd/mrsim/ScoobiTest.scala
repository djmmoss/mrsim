 import com.nicta.scoobi.Scoobi._
 import Reduction._

object ScoobiTest extends ScoobiApp {
    def run() {
        val lines = fromTextFile(args(0))

        val counts = lines.mapFlatten(_.split(" "))
            .map(w => (w,1))
            .groupByKey
            .combine(Sum.int)

        counts.toTextFile(args(1)).persist
    }
}
