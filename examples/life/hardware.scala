
import Chisel._

class LifeHardwareTests(c: Mapper[inBundle, outBundle]) extends Tester(c) {
    val in_1 = 0x3189544e
    poke(c.io.rx_dat.int, in_1)
    poke(c.io.rx_val, 1)
    step(1)
    poke(c.io.rx_val, 0)
    for (i <- 0 until 100) {
        step(1)
        peek(c.io.tx_dat.int)
        peek(c.io.tx_val)
    }
}

object LifeHardware {
    def main(args: Array[String]): Unit = {
        chiselMainTest(Array("--genHarness", "--test", "--compile"), () => Module(new Mapper(new inBundle, new outBundle))) {
            c => new LifeHardwareTests(c)
        }
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

    // Helper Funtion

    // Counter
    def counter(max: UInt) = {
        val x = Reg(init = UInt(0, max.getWidth))
        x := Mux(x === max, UInt(0), x + UInt(1))
        x
    }

    // Identifies the Neighbours around a particular cell
    def numberOfNeighboursFor(row: Int, col: Int): List[(Int, Int)] = areaAround(row, col) map wrap

    // Create a grid around the point
    def areaAround(row: Int, col: Int): List[(Int, Int)] = List((row-1, col-1), (row,col-1), (row+1,col-1), (row-1,col),(row, col), (row+1,col), (row-1,col+1), (row,col+1), (row+1,col+1))

    // Wrap the corners, i.e. the edges are all connected, think of the plane as a sphere.
    def wrap(cell: (Int, Int)): (Int, Int) = (((cell._1 + size) % size), ((cell._2 + size) % size))

    // 1-D id of the Cell
    def idx(i: Int, j: Int) = j*n + i

    // 1-D id of the cells neighbours
    def nbrIdx(di: Int, dj: Int) = (dj+1)*3 + (di+1)

    // 8x8 Grid - 64-Bits
    val n = 8
    val tot = n*n
    val size = n

    val io = new Bundle {
        val rx_dat = inBundle.clone.asInput
        val rx_val = Bool(INPUT)
        val rx_rdy = Bool(OUTPUT)
        val tx_dat = outBundle.clone.asOutput
        val tx_val = Bool(OUTPUT)
    }

    val is_full = Reg(init = Bool(false))
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

    io.rx_rdy := Bool(true)
    io.tx_val := Bool(true)
    io.tx_dat("int") := out.toBits().toUInt()
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
        val rx_val = Bool(INPUT)
        val tx_dat = UInt(OUTPUT, width = 64)
        val tx_val = Bool(OUTPUT)
    }

    io.tx_val := Mux(io.rx_val, Bool(false), Bool(true))
    io.tx_dat := io.rx_dat("int")
}

class decode[T <: Data](inBundle : T) extends Module {
    val io = new Bundle {
        val rx_dat = UInt(INPUT, width = 64)
        val rx_val = Bool(INPUT)
        val tx_dat = inBundle.clone.asOutput
        val tx_val = Bool(OUTPUT)
    }

    io.tx_val := Mux(io.rx_val, Bool(false), Bool(true))
    io.tx_dat("int") := io.rx_dat
}
