package com.usyd.mrsim.util

import Chisel._

class Fifo[T <: Data ](dataType : T, n : Int) extends Module {
  val io = new Bundle {
    val enq_val = Bool(INPUT)
    val enq_rdy = Bool(OUTPUT)
    val deq_val = Bool(OUTPUT)
    val deq_rdy = Bool(INPUT)
    val enq_dat = dataType.clone.asInput
    val deq_dat = dataType.clone.asOutput
  }
  val enq_ptr = Reg(init = UInt(0, n))
  val deq_ptr = Reg(init = UInt(0, n))
  val is_full = Reg(init = Bool(false))
  val is_empty = !is_full && (enq_ptr === deq_ptr)
  val do_enq = !is_full && io.enq_val
  val do_deq = io.deq_rdy && !is_empty
  val deq_ptr_inc = deq_ptr + UInt(1)
  val enq_ptr_inc = enq_ptr + UInt(1)
  val is_full_next = Mux(do_enq && ~do_deq && (enq_ptr_inc === deq_ptr), Bool(true), Mux(do_deq && is_full, Bool(false), is_full))
  enq_ptr := Mux(do_enq, enq_ptr_inc, enq_ptr)
  deq_ptr := Mux(do_deq, deq_ptr_inc, deq_ptr)
  is_full := is_full_next
  
  val ram = Mem(dataType.clone, n)
  when (do_enq) {
    ram(enq_ptr) := io.enq_dat
  }
  io.enq_rdy := !is_full
  io.deq_val := !is_empty
  io.deq_dat := Mux(do_deq, ram(deq_ptr), UInt(0))
}
