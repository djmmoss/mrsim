package com.usyd.mrsim.comm

import Chisel._

class ByteFifo(bw:Int) extends Module {
  //bw for Byte Width
  val io = new Bundle {
    val enq_val = Bool(INPUT)
    val enq_rdy = Bool(OUTPUT)
    val enq_dat = UInt(INPUT, width = 64)
    val enq_cnt = UInt(INPUT, width = 4)

    val deq_val = Bool(OUTPUT)
    val deq_rdy = Bool(INPUT)
    val deq_cnt = UInt(INPUT, width = log2Up(bw)+1)

    val cur_dat = UInt(OUTPUT, width = 8*bw)
    val cur_cnt = UInt(OUTPUT, width = log2Up(bw)+1)
  }

  val cur_fifo_r = Reg(init = UInt(0, width = 8*bw))
  val cur_cnt_r = Reg(init = UInt(0, width = log2Up(bw)+1))
  val cur_fifo_w = UInt(width = 8*bw)
  val cur_cnt_w = UInt(width = log2Up(bw)+1)

  cur_fifo_w := cur_fifo_r
  cur_cnt_w := cur_cnt_r

  val do_enq = io.enq_rdy && io.enq_val
  val do_deq = io.deq_rdy && io.deq_val

  val deq_rmn = cur_cnt_w - io.deq_cnt

  val enq_pos = UInt(width = log2Up(bw))
  when (!do_deq) { 
    enq_pos := cur_cnt_w 
  }.otherwise { 
    enq_pos := deq_rmn
  } //tbd

  io.enq_rdy := (cur_cnt_w <= UInt(8))
  io.deq_val := (cur_cnt_w >= io.deq_cnt)

  //build enqueue data pattern
  val enq_dat_mux = Vec.fill(log2Up(bw)+1){ UInt() }
  enq_dat_mux(0) := io.enq_dat
  for (i <- 0 until log2Up(bw)) {
    //enq_dat_mux(i) := enq_dat_mux(0) << UInt(8*i)
    enq_dat_mux(i+1) := Mux(enq_pos(i).toBool, 
      enq_dat_mux(i) << UInt(8*(scala.math.pow(2, i).toInt)), 
      enq_dat_mux(i)
    )
  }
  val enq_dat_shf = UInt(width = 8*bw)
  enq_dat_shf := enq_dat_mux(UInt(log2Up(bw)))

  //build shifted FIFO pattern
  val cur_fifo_mux = Vec.fill(log2Up(bw)+1){ UInt() }
  cur_fifo_mux(0) := cur_fifo_w
  for (i <- 0 until log2Up(bw)) {
    cur_fifo_mux(i+1) := Mux(io.deq_cnt(i).toBool,
      cur_fifo_mux(i) >> UInt(8*(scala.math.pow(2, i).toInt)),
      cur_fifo_mux(i)
    )
  }
  val cur_fifo_shf = UInt(width = 8*bw)
  cur_fifo_shf := cur_fifo_mux(UInt(log2Up(bw)))

  //build output select muxes
  val do_sel = Vec.fill(2){ Bool() }
  do_sel(0) := do_enq
  do_sel(1) := do_deq

  val nxt_fifo = Vec.fill(4){ UInt(width = 8*bw) }
  nxt_fifo(0) := cur_fifo_w
  nxt_fifo(1) := cur_fifo_w | enq_dat_shf
  nxt_fifo(2) := cur_fifo_shf //tbd
  nxt_fifo(3) := cur_fifo_shf | enq_dat_shf //tbd

  val nxt_cnt = Vec.fill(4){ UInt(width = log2Up(bw)+1) }
  nxt_cnt(0) := cur_cnt_w
  nxt_cnt(1) := cur_cnt_w + io.enq_cnt
  nxt_cnt(2) := deq_rmn
  nxt_cnt(3) := deq_rmn + io.enq_cnt

  when (Bool(true)) {
    //cur_fifo_r := MuxCase(cur_fifo_w, (do_sel zip nxt_fifo))
    //cur_fifo_r := nxt_fifo(do_sel.toBits.toUInt)
    cur_fifo_r := Mux(do_sel(1), 
      Mux(do_sel(0), nxt_fifo(3), nxt_fifo(2)),
      Mux(do_sel(0), nxt_fifo(1), nxt_fifo(0))
    )
    //cur_cnt_r := MuxCase(cur_cnt_w, (do_sel zip nxt_cnt))
    //cur_cnt_r := nxt_cnt(do_sel.toBits.toUInt)
    cur_cnt_r := Mux(do_sel(1), 
      Mux(do_sel(0), nxt_cnt(3), nxt_cnt(2)),
      Mux(do_sel(0), nxt_cnt(1), nxt_cnt(0))
    )
  }
  io.cur_dat := cur_fifo_r
  io.cur_cnt := cur_cnt_r
}
