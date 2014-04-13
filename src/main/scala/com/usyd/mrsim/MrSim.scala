package com.usyd.mrsim;

import com.usyd.mrsim.util.{FileLock, hexTranslator, hardwareTranslator};
import scala.collection.mutable._
import java.io._
import java.nio.file._
import scala.util.Random


/**
 * np_test
 * 
 * This is a Small object that tests the software -> simulation link
 * run make testInterface to execute
 *
 */
object np_test extends App with MrSim {
	for(i <- 0 until 10000000) {
		val num = Random.nextInt()
		val res = toHardware(false, Int.box(num));
    print(".")
	}
}


trait MrSim extends hardwareTranslator {
	val pipeIn = "/tmp/hw_in_pipe"
	val pipeOut = "/tmp/hw_out_pipe"
  val flagOutLoc = "/tmp/flagOut.lock"
	val GBFIFOSize = 16 // 1 = 4 Bits

  // Simulation Parameters
  val ctlFifo : Int = 4
  val numMap : Int = 1
  val simFifo: Int = 4
  val numRead: Int = 1

  def toHardware[K,T](end : Boolean, vals : AnyRef*) = {
   val wr = if(!end) encode(vals, GBFIFOSize) else vals.mkString
    println(wr)
   writeHW(wr.toString)
    decode(readHW())
  }

	def writeHW(in : String) {
		val lock = new FileLock(new File(pipeIn))
		while(lock.tryLock == false){}
		val send : Array[Array[Byte]] = partition(in)
		val pw = new RandomAccessFile(pipeIn, "rw")
		send.foreach(word => {pw.write(word) })
    pw.close()
		lock.destroy()
	}

	def readHW(): String = {
    val word = new Array[Byte](GBFIFOSize)
    val lock = new FileLock(new File(pipeOut))
    while(lock.tryLock == false){}
	val pw = new RandomAccessFile(pipeOut, "rw")
    pw.read(word)
    pw.close()
    lock.destroy()
    var decode = new String(word.map(_.toChar)) 
		decode
	}

	def partition(in : String): Array[Array[Byte]] = {
		val out = new ArrayBuffer[Array[Byte]]
		val tmp = new ArrayBuffer[Byte]
		val byteArray = in.getBytes()
		var i = 0
		for (i <- 0 until byteArray.length) {
			if (i%GBFIFOSize == 0){
				out.append(tmp.toArray)
				tmp.clear()
			}
			tmp.append(byteArray(i))
		}
		if(!tmp.isEmpty)
			out.append(tmp.toArray)
		out.toArray
	}
}
