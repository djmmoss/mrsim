package com.usyd.mrsim.util;

import java.io._
import scala.reflect.macros.Context

trait hardwareTranslator extends hexTranslator {
  // TODO: http://docs.scala-lang.org/overviews/macros/overview.html

// object Macros {
//  def translateHardware[T](function: T, params: Any*): Unit = macro translateHardware_impl

//  def translateHardware_impl(c : Context)(format: c.Expr[T], params: c.Expr[Any]*): c.Expr[Unit] = {

//  }
// }  

  def encode(in : Seq[AnyRef], size : Int) : String = {
    val tmp = new StringBuilder
    in.foreach(item => tmp.append(toHex(item, size)))
    tmp.toString
  }

  def decode(in : String) : String = {
    in
  }
}
