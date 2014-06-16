//package com.usyd.mrsim.example

//import com.nicta.scoobi.Scoobi._
//import Reduction._
//import com.usyd.mrsim._

//import scala.util.Random
//import scala.sys.process._

//object Life extends ScoobiApp with MrSim {
	//def run() {
		//val lines = fromTextFile(args(0))

		//val counts = lines
            //.map(w => LifeMapper(w.toLong))
		  //.groupByKey
		  //.combine(Sum.int)
		//counts.toTextFile(args(1)).persist
	//}

	//def LifeMapper(w : Long) : (String, Int) = {
        //val out =  toHardware(false, Long.box(w))
        //(out, 1)
	//}
//}
 
