package com.usyd.mrsim.util;

import java.io._

trait hexTranslator {
  
  def toHex[T](in : T, size : Int): String = 
    in match {
      case x: String => asciiToHex(x, size)
      case x: Int => intToHex(x, size)
      case x: Long => longToHex(x, size)
      case x: Float => floatToHex(x, size)
      case x: Double => doubleToHex(x, size)
      case x => "Unsupported Type"
    }

  def doubleToHex(in : Double, size : Int) : String = {
    padZero(java.lang.Double.doubleToRawLongBits(in).toHexString, size)
  }

  def hexToDouble(in : String) : Double = {
    java.lang.Double.longBitsToDouble(java.lang.Long.parseLong(in, 16))
  }

  def floatToHex(in: Float, size : Int) : String = {
    padZero(java.lang.Float.floatToRawIntBits(in).toHexString, size)
  }

  def hexToFloat(in: String) : Float = {
    java.lang.Float.intBitsToFloat(
      Integer.parseInt(
        Integer.toBinaryString(
          Integer.parseInt(in,16)),2))
  }

  def hexToAscii(in: String) : String = {
   import javax.xml.bind.DatatypeConverter
   new String(DatatypeConverter.parseHexBinary(in))
  }

  def asciiToHex(in : String, size : Int) : String = {
    padZero(in.toList.map(a => a.toByte).map("%02x".format(_)).mkString, size)
  }

  def hexToInt(in : String) : Int = {
    Integer.parseInt(in, 16)
  }

    def longToHex(in : Long, size : Int) : String = {
        padZero(in.toHexString, size)
    }

  def intToHex(in : Int, size : Int) : String = {
    padZero(Integer.toHexString(in).toString, size)
  }

  def padZero(in : String, size : Int) = {
    var res = in
    while (res.length%size != 0) {
      res = "0" + res
    }
    res
  }
}
