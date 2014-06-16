module Fifo_0(input clk, input reset,
    input  io_enq_val,
    output io_enq_rdy,
    output io_deq_val,
    input  io_deq_rdy,
    input [63:0] io_enq_dat,
    output[63:0] io_deq_dat
);

  wire[63:0] T0;
  wire[63:0] T1;
  reg [63:0] ram [3:0];
  wire[63:0] T2;
  wire[63:0] T3;
  wire do_enq;
  wire T4;
  reg[0:0] is_full;
  wire is_full_next;
  wire T5;
  wire T6;
  wire do_deq;
  wire T7;
  wire is_empty;
  wire T8;
  reg[3:0] deq_ptr;
  wire[3:0] T9;
  wire[3:0] deq_ptr_inc;
  reg[3:0] enq_ptr;
  wire[3:0] T10;
  wire[3:0] enq_ptr_inc;
  wire T11;
  wire T12;
  wire T13;
  wire T14;
  wire T15;
  wire[1:0] T16;
  wire[1:0] T17;
  wire T18;
  wire T19;

  assign io_deq_dat = T0;
  assign T0 = do_deq ? T1 : 64'h0;
  assign T1 = ram[T17];
  assign T3 = io_enq_dat;
  assign do_enq = T4 && io_enq_val;
  assign T4 = ! is_full;
  assign is_full_next = T12 ? 1'h1 : T5;
  assign T5 = T6 ? 1'h0 : is_full;
  assign T6 = do_deq && is_full;
  assign do_deq = io_deq_rdy && T7;
  assign T7 = ! is_empty;
  assign is_empty = T11 && T8;
  assign T8 = enq_ptr == deq_ptr;
  assign T9 = do_deq ? deq_ptr_inc : deq_ptr;
  assign deq_ptr_inc = deq_ptr + 4'h1;
  assign T10 = do_enq ? enq_ptr_inc : enq_ptr;
  assign enq_ptr_inc = enq_ptr + 4'h1;
  assign T11 = ! is_full;
  assign T12 = T14 && T13;
  assign T13 = enq_ptr_inc == deq_ptr;
  assign T14 = do_enq && T15;
  assign T15 = ~ do_deq;
  assign T16 = enq_ptr[1'h1:1'h0];
  assign T17 = deq_ptr[1'h1:1'h0];
  assign io_deq_val = T18;
  assign T18 = ! is_empty;
  assign io_enq_rdy = T19;
  assign T19 = ! is_full;

  always @(posedge clk) begin
    if (do_enq)
      ram[T16] <= T3;
    is_full <= reset ? 1'h0 : is_full_next;
    deq_ptr <= reset ? 4'h0 : T9;
    enq_ptr <= reset ? 4'h0 : T10;
  end
endmodule

module encode(
    input [7:0] io_rx_dat_int,
    input [7:0] io_rx_dat_int1,
    input [7:0] io_rx_dat_cent,
    output io_rx_rdy,
    input  io_rx_val,
    output[63:0] io_tx_dat,
    output io_tx_val
);

  wire[63:0] T0;
  wire[31:0] T1;
  wire[23:0] T2;
  wire[15:0] T3;

  assign io_tx_val = io_rx_val;
  assign io_tx_dat = T0;
  assign T0 = {32'h0, T1};
  assign T1 = {T2, 8'hff};
  assign T2 = {T3, io_rx_dat_cent};
  assign T3 = {io_rx_dat_int1, io_rx_dat_int};
  assign io_rx_rdy = 1'h1;
endmodule

module decode(
    input [63:0] io_rx_dat,
    output io_rx_rdy,
    input  io_rx_val,
    output[7:0] io_tx_dat_int,
    output[7:0] io_tx_dat_int1,
    output[7:0] io_tx_dat_c11,
    output[7:0] io_tx_dat_c12,
    output[7:0] io_tx_dat_c21,
    output[7:0] io_tx_dat_c22,
    output[7:0] io_tx_dat_c31,
    output[7:0] io_tx_dat_c32,
    output io_tx_val
);

  wire[7:0] T0;
  wire[7:0] T1;
  wire[7:0] T2;
  wire[7:0] T3;
  wire[7:0] T4;
  wire[7:0] T5;
  wire[7:0] T6;
  wire[7:0] T7;

  assign io_tx_val = io_rx_val;
  assign io_tx_dat_c32 = T0;
  assign T0 = io_rx_dat[6'h2f:6'h28];
  assign io_tx_dat_c31 = T1;
  assign T1 = io_rx_dat[6'h27:6'h20];
  assign io_tx_dat_c22 = T2;
  assign T2 = io_rx_dat[5'h1f:5'h18];
  assign io_tx_dat_c21 = T3;
  assign T3 = io_rx_dat[5'h17:5'h10];
  assign io_tx_dat_c12 = T4;
  assign T4 = io_rx_dat[4'hf:4'h8];
  assign io_tx_dat_c11 = T5;
  assign T5 = io_rx_dat[3'h7:1'h0];
  assign io_tx_dat_int1 = T6;
  assign T6 = io_rx_dat[6'h3f:6'h38];
  assign io_tx_dat_int = T7;
  assign T7 = io_rx_dat[6'h37:6'h30];
  assign io_rx_rdy = 1'h1;
endmodule

module Fifo_1(input clk, input reset,
    input  io_enq_val,
    output io_enq_rdy,
    output io_deq_val,
    input  io_deq_rdy,
    input [7:0] io_enq_dat_int,
    input [7:0] io_enq_dat_int1,
    input [7:0] io_enq_dat_c11,
    input [7:0] io_enq_dat_c12,
    input [7:0] io_enq_dat_c21,
    input [7:0] io_enq_dat_c22,
    input [7:0] io_enq_dat_c31,
    input [7:0] io_enq_dat_c32,
    output[7:0] io_deq_dat_int,
    output[7:0] io_deq_dat_int1,
    output[7:0] io_deq_dat_c11,
    output[7:0] io_deq_dat_c12,
    output[7:0] io_deq_dat_c21,
    output[7:0] io_deq_dat_c22,
    output[7:0] io_deq_dat_c31,
    output[7:0] io_deq_dat_c32
);

  wire[7:0] T0;
  wire[63:0] T1;
  wire[63:0] T2;
  wire[7:0] T3;
  wire[63:0] T4;
  reg [63:0] ram [3:0];
  wire[63:0] T5;
  wire[63:0] T6;
  wire[63:0] T7;
  wire[55:0] T8;
  wire[47:0] T9;
  wire[39:0] T10;
  wire[31:0] T11;
  wire[23:0] T12;
  wire[15:0] T13;
  wire do_enq;
  wire T14;
  reg[0:0] is_full;
  wire is_full_next;
  wire T15;
  wire T16;
  wire do_deq;
  wire T17;
  wire is_empty;
  wire T18;
  reg[3:0] deq_ptr;
  wire[3:0] T19;
  wire[3:0] deq_ptr_inc;
  reg[3:0] enq_ptr;
  wire[3:0] T20;
  wire[3:0] enq_ptr_inc;
  wire T21;
  wire T22;
  wire T23;
  wire T24;
  wire T25;
  wire[1:0] T26;
  wire[1:0] T27;
  wire[55:0] T28;
  wire[7:0] T29;
  wire[47:0] T30;
  wire[7:0] T31;
  wire[39:0] T32;
  wire[7:0] T33;
  wire[31:0] T34;
  wire[7:0] T35;
  wire[23:0] T36;
  wire[7:0] T37;
  wire[15:0] T38;
  wire[7:0] T39;
  wire[7:0] T40;
  wire[7:0] T41;
  wire[7:0] T42;
  wire[7:0] T43;
  wire[7:0] T44;
  wire[7:0] T45;
  wire[7:0] T46;
  wire[7:0] T47;
  wire T48;
  wire T49;

  assign io_deq_dat_c32 = T0;
  assign T0 = T1[3'h7:1'h0];
  assign T1 = do_deq ? T2 : 64'h0;
  assign T2 = {T28, T3};
  assign T3 = T4[3'h7:1'h0];
  assign T4 = ram[T27];
  assign T6 = T7;
  assign T7 = {T8, io_enq_dat_c32};
  assign T8 = {T9, io_enq_dat_c31};
  assign T9 = {T10, io_enq_dat_c22};
  assign T10 = {T11, io_enq_dat_c21};
  assign T11 = {T12, io_enq_dat_c12};
  assign T12 = {T13, io_enq_dat_c11};
  assign T13 = {io_enq_dat_int, io_enq_dat_int1};
  assign do_enq = T14 && io_enq_val;
  assign T14 = ! is_full;
  assign is_full_next = T22 ? 1'h1 : T15;
  assign T15 = T16 ? 1'h0 : is_full;
  assign T16 = do_deq && is_full;
  assign do_deq = io_deq_rdy && T17;
  assign T17 = ! is_empty;
  assign is_empty = T21 && T18;
  assign T18 = enq_ptr == deq_ptr;
  assign T19 = do_deq ? deq_ptr_inc : deq_ptr;
  assign deq_ptr_inc = deq_ptr + 4'h1;
  assign T20 = do_enq ? enq_ptr_inc : enq_ptr;
  assign enq_ptr_inc = enq_ptr + 4'h1;
  assign T21 = ! is_full;
  assign T22 = T24 && T23;
  assign T23 = enq_ptr_inc == deq_ptr;
  assign T24 = do_enq && T25;
  assign T25 = ~ do_deq;
  assign T26 = enq_ptr[1'h1:1'h0];
  assign T27 = deq_ptr[1'h1:1'h0];
  assign T28 = {T30, T29};
  assign T29 = T4[4'hf:4'h8];
  assign T30 = {T32, T31};
  assign T31 = T4[5'h17:5'h10];
  assign T32 = {T34, T33};
  assign T33 = T4[5'h1f:5'h18];
  assign T34 = {T36, T35};
  assign T35 = T4[6'h27:6'h20];
  assign T36 = {T38, T37};
  assign T37 = T4[6'h2f:6'h28];
  assign T38 = {T40, T39};
  assign T39 = T4[6'h37:6'h30];
  assign T40 = T4[6'h3f:6'h38];
  assign io_deq_dat_c31 = T41;
  assign T41 = T1[4'hf:4'h8];
  assign io_deq_dat_c22 = T42;
  assign T42 = T1[5'h17:5'h10];
  assign io_deq_dat_c21 = T43;
  assign T43 = T1[5'h1f:5'h18];
  assign io_deq_dat_c12 = T44;
  assign T44 = T1[6'h27:6'h20];
  assign io_deq_dat_c11 = T45;
  assign T45 = T1[6'h2f:6'h28];
  assign io_deq_dat_int1 = T46;
  assign T46 = T1[6'h37:6'h30];
  assign io_deq_dat_int = T47;
  assign T47 = T1[6'h3f:6'h38];
  assign io_deq_val = T48;
  assign T48 = ! is_empty;
  assign io_enq_rdy = T49;
  assign T49 = ! is_full;

  always @(posedge clk) begin
    if (do_enq)
      ram[T26] <= T6;
    is_full <= reset ? 1'h0 : is_full_next;
    deq_ptr <= reset ? 4'h0 : T19;
    enq_ptr <= reset ? 4'h0 : T20;
  end
endmodule

module Fifo_2(input clk, input reset,
    input  io_enq_val,
    output io_enq_rdy,
    output io_deq_val,
    input  io_deq_rdy,
    input [7:0] io_enq_dat_int,
    input [7:0] io_enq_dat_int1,
    input [7:0] io_enq_dat_cent,
    output[7:0] io_deq_dat_int,
    output[7:0] io_deq_dat_int1,
    output[7:0] io_deq_dat_cent
);

  wire[7:0] T0;
  wire[23:0] T1;
  wire[23:0] T2;
  wire[7:0] T3;
  wire[23:0] T4;
  reg [23:0] ram [3:0];
  wire[23:0] T5;
  wire[23:0] T6;
  wire[23:0] T7;
  wire[15:0] T8;
  wire do_enq;
  wire T9;
  reg[0:0] is_full;
  wire is_full_next;
  wire T10;
  wire T11;
  wire do_deq;
  wire T12;
  wire is_empty;
  wire T13;
  reg[3:0] deq_ptr;
  wire[3:0] T14;
  wire[3:0] deq_ptr_inc;
  reg[3:0] enq_ptr;
  wire[3:0] T15;
  wire[3:0] enq_ptr_inc;
  wire T16;
  wire T17;
  wire T18;
  wire T19;
  wire T20;
  wire[1:0] T21;
  wire[1:0] T22;
  wire[15:0] T23;
  wire[7:0] T24;
  wire[7:0] T25;
  wire[7:0] T26;
  wire[7:0] T27;
  wire T28;
  wire T29;

  assign io_deq_dat_cent = T0;
  assign T0 = T1[3'h7:1'h0];
  assign T1 = do_deq ? T2 : 24'h0;
  assign T2 = {T23, T3};
  assign T3 = T4[3'h7:1'h0];
  assign T4 = ram[T22];
  assign T6 = T7;
  assign T7 = {T8, io_enq_dat_cent};
  assign T8 = {io_enq_dat_int, io_enq_dat_int1};
  assign do_enq = T9 && io_enq_val;
  assign T9 = ! is_full;
  assign is_full_next = T17 ? 1'h1 : T10;
  assign T10 = T11 ? 1'h0 : is_full;
  assign T11 = do_deq && is_full;
  assign do_deq = io_deq_rdy && T12;
  assign T12 = ! is_empty;
  assign is_empty = T16 && T13;
  assign T13 = enq_ptr == deq_ptr;
  assign T14 = do_deq ? deq_ptr_inc : deq_ptr;
  assign deq_ptr_inc = deq_ptr + 4'h1;
  assign T15 = do_enq ? enq_ptr_inc : enq_ptr;
  assign enq_ptr_inc = enq_ptr + 4'h1;
  assign T16 = ! is_full;
  assign T17 = T19 && T18;
  assign T18 = enq_ptr_inc == deq_ptr;
  assign T19 = do_enq && T20;
  assign T20 = ~ do_deq;
  assign T21 = enq_ptr[1'h1:1'h0];
  assign T22 = deq_ptr[1'h1:1'h0];
  assign T23 = {T25, T24};
  assign T24 = T4[4'hf:4'h8];
  assign T25 = T4[5'h17:5'h10];
  assign io_deq_dat_int1 = T26;
  assign T26 = T1[4'hf:4'h8];
  assign io_deq_dat_int = T27;
  assign T27 = T1[5'h17:5'h10];
  assign io_deq_val = T28;
  assign T28 = ! is_empty;
  assign io_enq_rdy = T29;
  assign T29 = ! is_full;

  always @(posedge clk) begin
    if (do_enq)
      ram[T21] <= T6;
    is_full <= reset ? 1'h0 : is_full_next;
    deq_ptr <= reset ? 4'h0 : T14;
    enq_ptr <= reset ? 4'h0 : T15;
  end
endmodule

module EucDistBlock(
    input [63:0] io_point_0,
    input [63:0] io_point_1_,
    input [63:0] io_centroid_0___,
    input [63:0] io_centroid_1____,
    output[63:0] io_out
);

  wire[63:0] T0;
  wire[63:0] distSum_1;
  wire[63:0] T1;
  wire[127:0] T2;
  wire[63:0] T3;
  wire[63:0] T4;
  wire[63:0] distSum_0;
  wire[63:0] T5;
  wire[127:0] T6;
  wire[63:0] T7;
  wire[63:0] T8;

  assign io_out = T0;
  assign T0 = distSum_0 + distSum_1;
  assign distSum_1 = T1;
  assign T1 = T2[6'h3f:1'h0];
  assign T2 = T4 * T3;
  assign T3 = io_centroid_1____ - io_point_1_;
  assign T4 = io_centroid_1____ - io_point_1_;
  assign distSum_0 = T5;
  assign T5 = T6[6'h3f:1'h0];
  assign T6 = T8 * T7;
  assign T7 = io_centroid_0___ - io_point_0;
  assign T8 = io_centroid_0___ - io_point_0;
endmodule

module ClosestCentreN(
    input [63:0] io_in_0_,
    input [63:0] io_in_1__,
    input [63:0] io_in_2___,
    output[63:0] io_out
);

  wire[63:0] T0;
  wire[1:0] T1;
  wire[1:0] T2;
  wire T3;
  wire[63:0] T4;
  wire[63:0] T5;
  wire T6;
  wire T7;
  wire T8;
  wire[63:0] T9;
  wire[63:0] T10;
  wire T11;
  wire T12;

  assign io_out = T0;
  assign T0 = {62'h0, T1};
  assign T1 = T8 ? 2'h2 : T2;
  assign T2 = {1'h0, T3};
  assign T3 = T4 == io_in_1__;
  assign T4 = T7 ? T5 : io_in_2___;
  assign T5 = T6 ? io_in_0_ : io_in_1__;
  assign T6 = io_in_0_ <= io_in_1__;
  assign T7 = T5 <= io_in_2___;
  assign T8 = T9 == io_in_2___;
  assign T9 = T12 ? T10 : io_in_2___;
  assign T10 = T11 ? io_in_0_ : io_in_1__;
  assign T11 = io_in_0_ <= io_in_1__;
  assign T12 = T10 <= io_in_2___;
endmodule

module Mapper(
    input [7:0] io_rx_dat_int,
    input [7:0] io_rx_dat_int1,
    input [7:0] io_rx_dat_c11,
    input [7:0] io_rx_dat_c12,
    input [7:0] io_rx_dat_c21,
    input [7:0] io_rx_dat_c22,
    input [7:0] io_rx_dat_c31,
    input [7:0] io_rx_dat_c32,
    input  io_rx_val,
    output io_rx_rdy,
    output[7:0] io_tx_dat_int,
    output[7:0] io_tx_dat_int1,
    output[7:0] io_tx_dat_cent,
    output io_tx_val
);

  wire[63:0] EucDistBlock_2_io_out;
  wire[63:0] EucDistBlock_1_io_out;
  wire[63:0] EucDistBlock_0_io_out;
  wire[63:0] T0;
  wire[63:0] T1;
  wire[63:0] T2;
  wire[63:0] T3;
  wire[63:0] T4;
  wire[63:0] T5;
  wire[63:0] T6;
  wire[63:0] T7;
  wire[63:0] T8;
  wire[63:0] T9;
  wire[63:0] T10;
  wire[63:0] T11;
  wire T12;
  wire T13;
  wire[7:0] T14;
  wire[63:0] ClosestCentreN_io_out;

  assign T0 = {56'h0, io_rx_dat_c32};
  assign T1 = {56'h0, io_rx_dat_c31};
  assign T2 = {56'h0, io_rx_dat_int1};
  assign T3 = {56'h0, io_rx_dat_int};
  assign T4 = {56'h0, io_rx_dat_c22};
  assign T5 = {56'h0, io_rx_dat_c21};
  assign T6 = {56'h0, io_rx_dat_int1};
  assign T7 = {56'h0, io_rx_dat_int};
  assign T8 = {56'h0, io_rx_dat_c12};
  assign T9 = {56'h0, io_rx_dat_c11};
  assign T10 = {56'h0, io_rx_dat_int1};
  assign T11 = {56'h0, io_rx_dat_int};
  assign io_tx_val = T12;
  assign T12 = T13 ? 1'h0 : io_rx_val;
  assign T13 = ! io_rx_val;
  assign io_tx_dat_cent = T14;
  assign T14 = ClosestCentreN_io_out[3'h7:1'h0];
  assign io_tx_dat_int1 = io_rx_dat_int1;
  assign io_tx_dat_int = io_rx_dat_int;
  assign io_rx_rdy = 1'h1;
  EucDistBlock EucDistBlock_0(
       .io_point_0( T11 ),
       .io_point_1_( T10 ),
       .io_centroid_0___( T9 ),
       .io_centroid_1____( T8 ),
       .io_out( EucDistBlock_0_io_out )
  );
  EucDistBlock EucDistBlock_1(
       .io_point_0( T7 ),
       .io_point_1_( T6 ),
       .io_centroid_0___( T5 ),
       .io_centroid_1____( T4 ),
       .io_out( EucDistBlock_1_io_out )
  );
  EucDistBlock EucDistBlock_2(
       .io_point_0( T3 ),
       .io_point_1_( T2 ),
       .io_centroid_0___( T1 ),
       .io_centroid_1____( T0 ),
       .io_out( EucDistBlock_2_io_out )
  );
  ClosestCentreN ClosestCentreN(
       .io_in_0_( EucDistBlock_0_io_out ),
       .io_in_1__( EucDistBlock_1_io_out ),
       .io_in_2___( EucDistBlock_2_io_out ),
       .io_out( ClosestCentreN_io_out )
  );
endmodule

module Controller(input clk, input reset,
    input [7:0] io_rx_dat_int,
    input [7:0] io_rx_dat_int1,
    input [7:0] io_rx_dat_c11,
    input [7:0] io_rx_dat_c12,
    input [7:0] io_rx_dat_c21,
    input [7:0] io_rx_dat_c22,
    input [7:0] io_rx_dat_c31,
    input [7:0] io_rx_dat_c32,
    input  io_rx_val,
    output[7:0] io_tx_dat_int,
    output[7:0] io_tx_dat_int1,
    output[7:0] io_tx_dat_cent,
    output io_tx_val
);

  wire T0;
  wire T1;
  wire Fifo_0_io_deq_val;
  wire Mapper_io_rx_rdy;
  wire T2;
  wire[7:0] Fifo_0_io_deq_dat_c32;
  wire[7:0] Fifo_0_io_deq_dat_c31;
  wire[7:0] Fifo_0_io_deq_dat_c22;
  wire[7:0] Fifo_0_io_deq_dat_c21;
  wire[7:0] Fifo_0_io_deq_dat_c12;
  wire[7:0] Fifo_0_io_deq_dat_c11;
  wire[7:0] Fifo_0_io_deq_dat_int1;
  wire[7:0] Fifo_0_io_deq_dat_int;
  wire[7:0] Mapper_io_tx_dat_cent;
  wire[7:0] Mapper_io_tx_dat_int1;
  wire[7:0] Mapper_io_tx_dat_int;
  wire T3;
  wire Fifo_1_io_deq_val;
  wire T4;
  wire T5;
  wire T6;
  wire Fifo_1_io_enq_rdy;
  wire Mapper_io_tx_val;
  wire T7;
  wire T8;
  wire T9;
  wire T10;
  wire Fifo_0_io_enq_rdy;
  wire T11;
  wire T12;
  reg[0:0] inCounter;
  wire T13;
  wire T14;
  wire T15;
  wire T16;
  wire[7:0] T17;
  wire[7:0] Fifo_1_io_deq_dat_cent;
  wire[7:0] T18;
  wire[7:0] Fifo_1_io_deq_dat_int1;
  wire[7:0] T19;
  wire[7:0] Fifo_1_io_deq_dat_int;

  assign T0 = T2 ? 1'h0 : T1;
  assign T1 = Mapper_io_rx_rdy && Fifo_0_io_deq_val;
  assign T2 = ! T1;
  assign T3 = T4 ? 1'h0 : Fifo_1_io_deq_val;
  assign T4 = ! Fifo_1_io_deq_val;
  assign T5 = T7 ? 1'h0 : T6;
  assign T6 = Mapper_io_tx_val && Fifo_1_io_enq_rdy;
  assign T7 = ! T6;
  assign T8 = T2 ? 1'h0 : T1;
  assign T9 = T16 ? 1'h0 : T10;
  assign T10 = T11 && Fifo_0_io_enq_rdy;
  assign T11 = T12 && io_rx_val;
  assign T12 = 1'h0 == inCounter;
  assign T13 = T15 ? 1'h0 : T14;
  assign T14 = inCounter + 1'h1;
  assign T15 = inCounter == 1'h0;
  assign T16 = ! T10;
  assign io_tx_val = Fifo_1_io_deq_val;
  assign io_tx_dat_cent = T17;
  assign T17 = Fifo_1_io_deq_val ? Fifo_1_io_deq_dat_cent : 8'h0;
  assign io_tx_dat_int1 = T18;
  assign T18 = Fifo_1_io_deq_val ? Fifo_1_io_deq_dat_int1 : 8'h0;
  assign io_tx_dat_int = T19;
  assign T19 = Fifo_1_io_deq_val ? Fifo_1_io_deq_dat_int : 8'h0;
  Fifo_1 Fifo_0(.clk(clk), .reset(reset),
       .io_enq_val( T9 ),
       .io_enq_rdy( Fifo_0_io_enq_rdy ),
       .io_deq_val( Fifo_0_io_deq_val ),
       .io_deq_rdy( T8 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_enq_dat_int1( io_rx_dat_int1 ),
       .io_enq_dat_c11( io_rx_dat_c11 ),
       .io_enq_dat_c12( io_rx_dat_c12 ),
       .io_enq_dat_c21( io_rx_dat_c21 ),
       .io_enq_dat_c22( io_rx_dat_c22 ),
       .io_enq_dat_c31( io_rx_dat_c31 ),
       .io_enq_dat_c32( io_rx_dat_c32 ),
       .io_deq_dat_int( Fifo_0_io_deq_dat_int ),
       .io_deq_dat_int1( Fifo_0_io_deq_dat_int1 ),
       .io_deq_dat_c11( Fifo_0_io_deq_dat_c11 ),
       .io_deq_dat_c12( Fifo_0_io_deq_dat_c12 ),
       .io_deq_dat_c21( Fifo_0_io_deq_dat_c21 ),
       .io_deq_dat_c22( Fifo_0_io_deq_dat_c22 ),
       .io_deq_dat_c31( Fifo_0_io_deq_dat_c31 ),
       .io_deq_dat_c32( Fifo_0_io_deq_dat_c32 )
  );
  Fifo_2 Fifo_1(.clk(clk), .reset(reset),
       .io_enq_val( T5 ),
       .io_enq_rdy( Fifo_1_io_enq_rdy ),
       .io_deq_val( Fifo_1_io_deq_val ),
       .io_deq_rdy( T3 ),
       .io_enq_dat_int( Mapper_io_tx_dat_int ),
       .io_enq_dat_int1( Mapper_io_tx_dat_int1 ),
       .io_enq_dat_cent( Mapper_io_tx_dat_cent ),
       .io_deq_dat_int( Fifo_1_io_deq_dat_int ),
       .io_deq_dat_int1( Fifo_1_io_deq_dat_int1 ),
       .io_deq_dat_cent( Fifo_1_io_deq_dat_cent )
  );
  Mapper Mapper(
       .io_rx_dat_int( Fifo_0_io_deq_dat_int ),
       .io_rx_dat_int1( Fifo_0_io_deq_dat_int1 ),
       .io_rx_dat_c11( Fifo_0_io_deq_dat_c11 ),
       .io_rx_dat_c12( Fifo_0_io_deq_dat_c12 ),
       .io_rx_dat_c21( Fifo_0_io_deq_dat_c21 ),
       .io_rx_dat_c22( Fifo_0_io_deq_dat_c22 ),
       .io_rx_dat_c31( Fifo_0_io_deq_dat_c31 ),
       .io_rx_dat_c32( Fifo_0_io_deq_dat_c32 ),
       .io_rx_val( T0 ),
       .io_rx_rdy( Mapper_io_rx_rdy ),
       .io_tx_dat_int( Mapper_io_tx_dat_int ),
       .io_tx_dat_int1( Mapper_io_tx_dat_int1 ),
       .io_tx_dat_cent( Mapper_io_tx_dat_cent ),
       .io_tx_val( Mapper_io_tx_val )
  );

  always @(posedge clk) begin
    if(reset) begin
      inCounter <= 1'h0;
    end else if(T10) begin
      inCounter <= T13;
    end
  end
endmodule

module MrSimSimulation(input clk, input reset,
    input [63:0] io_enq_dat,
    input  io_enq_val,
    output io_enq_rdy,
    output[63:0] io_deq_dat,
    output io_deq_val,
    input  io_deq_rdy
);

  wire decode_io_tx_val;
  wire[7:0] decode_io_tx_dat_c32;
  wire[7:0] decode_io_tx_dat_c31;
  wire[7:0] decode_io_tx_dat_c22;
  wire[7:0] decode_io_tx_dat_c21;
  wire[7:0] decode_io_tx_dat_c12;
  wire[7:0] decode_io_tx_dat_c11;
  wire[7:0] decode_io_tx_dat_int1;
  wire[7:0] decode_io_tx_dat_int;
  wire T0;
  wire decode_io_rx_rdy;
  wire Fifo_0_io_deq_val;
  wire[63:0] Fifo_0_io_deq_dat;
  wire Controller_io_tx_val;
  wire[7:0] Controller_io_tx_dat_cent;
  wire[7:0] Controller_io_tx_dat_int1;
  wire[7:0] Controller_io_tx_dat_int;
  wire[63:0] encode_io_tx_dat;
  wire encode_io_tx_val;
  wire T1;
  wire Fifo_1_io_deq_val;
  wire[63:0] Fifo_1_io_deq_dat;
  wire Fifo_0_io_enq_rdy;

  assign T0 = Fifo_0_io_deq_val && decode_io_rx_rdy;
  assign T1 = decode_io_rx_rdy && Fifo_0_io_deq_val;
  assign io_deq_val = Fifo_1_io_deq_val;
  assign io_deq_dat = Fifo_1_io_deq_dat;
  assign io_enq_rdy = Fifo_0_io_enq_rdy;
  Fifo_0 Fifo_0(.clk(clk), .reset(reset),
       .io_enq_val( io_enq_val ),
       .io_enq_rdy( Fifo_0_io_enq_rdy ),
       .io_deq_val( Fifo_0_io_deq_val ),
       .io_deq_rdy( T1 ),
       .io_enq_dat( io_enq_dat ),
       .io_deq_dat( Fifo_0_io_deq_dat )
  );
  Fifo_0 Fifo_1(.clk(clk), .reset(reset),
       .io_enq_val( encode_io_tx_val ),
       //.io_enq_rdy(  )
       .io_deq_val( Fifo_1_io_deq_val ),
       .io_deq_rdy( io_deq_rdy ),
       .io_enq_dat( encode_io_tx_dat ),
       .io_deq_dat( Fifo_1_io_deq_dat )
  );
  encode encode(
       .io_rx_dat_int( Controller_io_tx_dat_int ),
       .io_rx_dat_int1( Controller_io_tx_dat_int1 ),
       .io_rx_dat_cent( Controller_io_tx_dat_cent ),
       //.io_rx_rdy(  )
       .io_rx_val( Controller_io_tx_val ),
       .io_tx_dat( encode_io_tx_dat ),
       .io_tx_val( encode_io_tx_val )
  );
  decode decode(
       .io_rx_dat( Fifo_0_io_deq_dat ),
       .io_rx_rdy( decode_io_rx_rdy ),
       .io_rx_val( T0 ),
       .io_tx_dat_int( decode_io_tx_dat_int ),
       .io_tx_dat_int1( decode_io_tx_dat_int1 ),
       .io_tx_dat_c11( decode_io_tx_dat_c11 ),
       .io_tx_dat_c12( decode_io_tx_dat_c12 ),
       .io_tx_dat_c21( decode_io_tx_dat_c21 ),
       .io_tx_dat_c22( decode_io_tx_dat_c22 ),
       .io_tx_dat_c31( decode_io_tx_dat_c31 ),
       .io_tx_dat_c32( decode_io_tx_dat_c32 ),
       .io_tx_val( decode_io_tx_val )
  );
  Controller Controller(.clk(clk), .reset(reset),
       .io_rx_dat_int( decode_io_tx_dat_int ),
       .io_rx_dat_int1( decode_io_tx_dat_int1 ),
       .io_rx_dat_c11( decode_io_tx_dat_c11 ),
       .io_rx_dat_c12( decode_io_tx_dat_c12 ),
       .io_rx_dat_c21( decode_io_tx_dat_c21 ),
       .io_rx_dat_c22( decode_io_tx_dat_c22 ),
       .io_rx_dat_c31( decode_io_tx_dat_c31 ),
       .io_rx_dat_c32( decode_io_tx_dat_c32 ),
       .io_rx_val( decode_io_tx_val ),
       .io_tx_dat_int( Controller_io_tx_dat_int ),
       .io_tx_dat_int1( Controller_io_tx_dat_int1 ),
       .io_tx_dat_cent( Controller_io_tx_dat_cent ),
       .io_tx_val( Controller_io_tx_val )
  );
endmodule

