module Fifo_0(input clk, input reset,
    input  io_enq_val,
    output io_enq_rdy,
    output io_deq_val,
    input  io_deq_rdy,
    input [63:0] io_enq_dat,
    output[63:0] io_deq_dat
);

  wire[63:0] T0;
  reg [63:0] ram [3:0];
  wire[63:0] T1;
  wire[63:0] T2;
  wire do_enq;
  wire T3;
  reg[0:0] is_full;
  wire is_full_next;
  wire T4;
  wire T5;
  wire do_deq;
  wire T6;
  wire is_empty;
  wire T7;
  reg[3:0] deq_ptr;
  wire[3:0] T8;
  wire[3:0] deq_ptr_inc;
  reg[3:0] enq_ptr;
  wire[3:0] T9;
  wire[3:0] enq_ptr_inc;
  wire T10;
  wire T11;
  wire T12;
  wire T13;
  wire T14;
  wire[1:0] T15;
  wire[1:0] T16;
  wire T17;
  wire T18;

  assign io_deq_dat = T0;
  assign T0 = ram[T16];
  assign T2 = io_enq_dat;
  assign do_enq = T3 && io_enq_val;
  assign T3 = ! is_full;
  assign is_full_next = T11 ? 1'h1 : T4;
  assign T4 = T5 ? 1'h0 : is_full;
  assign T5 = do_deq && is_full;
  assign do_deq = io_deq_rdy && T6;
  assign T6 = ! is_empty;
  assign is_empty = T10 && T7;
  assign T7 = enq_ptr == deq_ptr;
  assign T8 = do_deq ? deq_ptr_inc : deq_ptr;
  assign deq_ptr_inc = deq_ptr + 4'h1;
  assign T9 = do_enq ? enq_ptr_inc : enq_ptr;
  assign enq_ptr_inc = enq_ptr + 4'h1;
  assign T10 = ! is_full;
  assign T11 = T13 && T12;
  assign T12 = enq_ptr_inc == deq_ptr;
  assign T13 = do_enq && T14;
  assign T14 = ~ do_deq;
  assign T15 = enq_ptr[1'h1:1'h0];
  assign T16 = deq_ptr[1'h1:1'h0];
  assign io_deq_val = T17;
  assign T17 = ! is_empty;
  assign io_enq_rdy = T18;
  assign T18 = ! is_full;

  always @(posedge clk) begin
    if (do_enq)
      ram[T15] <= T2;
    is_full <= reset ? 1'h0 : is_full_next;
    deq_ptr <= reset ? 4'h0 : T8;
    enq_ptr <= reset ? 4'h0 : T9;
  end
endmodule

module encode(
    input [63:0] io_rx_dat_int,
    output io_rx_rdy,
    input  io_rx_val,
    output[63:0] io_tx_dat,
    output io_tx_val
);


  assign io_tx_val = io_rx_val;
  assign io_tx_dat = io_rx_dat_int;
  assign io_rx_rdy = 1'h1;
endmodule

module decode(
    input [63:0] io_rx_dat,
    output io_rx_rdy,
    input  io_rx_val,
    output[63:0] io_tx_dat_int,
    output io_tx_val
);


  assign io_tx_val = io_rx_val;
  assign io_tx_dat_int = io_rx_dat;
  assign io_rx_rdy = 1'h1;
endmodule

module Fifo_1(input clk, input reset,
    input  io_enq_val,
    output io_enq_rdy,
    output io_deq_val,
    input  io_deq_rdy,
    input [63:0] io_enq_dat_int,
    output[63:0] io_deq_dat_int
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

  assign io_deq_dat_int = T0;
  assign T0 = T1[6'h3f:1'h0];
  assign T1 = ram[T17];
  assign T3 = io_enq_dat_int;
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

module Mapper(
    input [63:0] io_rx_dat_int,
    input  io_rx_val,
    output io_rx_rdy,
    output[63:0] io_tx_dat_int,
    output io_tx_val
);

  wire T0;
  wire T1;

  assign io_tx_val = T0;
  assign T0 = T1 ? 1'h0 : io_rx_val;
  assign T1 = ! io_rx_val;
  assign io_tx_dat_int = io_rx_dat_int;
  assign io_rx_rdy = 1'h1;
endmodule

module Controller(input clk, input reset,
    input [63:0] io_rx_dat_int,
    input  io_rx_val,
    output[63:0] io_tx_dat_int,
    output io_tx_val
);

  wire T0;
  wire T1;
  wire Fifo_0_io_deq_val;
  wire Mapper_io_rx_rdy;
  wire T2;
  wire[63:0] Fifo_0_io_deq_dat_int;
  wire[63:0] Mapper_io_tx_dat_int;
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
  wire[63:0] T17;
  wire[63:0] outNullBundle_int;
  wire[63:0] Fifo_1_io_deq_dat_int;

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
  assign io_tx_dat_int = T17;
  assign T17 = Fifo_1_io_deq_val ? Fifo_1_io_deq_dat_int : outNullBundle_int;
  Fifo_1 Fifo_0(.clk(clk), .reset(reset),
       .io_enq_val( T9 ),
       .io_enq_rdy( Fifo_0_io_enq_rdy ),
       .io_deq_val( Fifo_0_io_deq_val ),
       .io_deq_rdy( T8 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_deq_dat_int( Fifo_0_io_deq_dat_int )
  );
  Fifo_1 Fifo_1(.clk(clk), .reset(reset),
       .io_enq_val( T5 ),
       .io_enq_rdy( Fifo_1_io_enq_rdy ),
       .io_deq_val( Fifo_1_io_deq_val ),
       .io_deq_rdy( T3 ),
       .io_enq_dat_int( Mapper_io_tx_dat_int ),
       .io_deq_dat_int( Fifo_1_io_deq_dat_int )
  );
  Mapper Mapper(
       .io_rx_dat_int( Fifo_0_io_deq_dat_int ),
       .io_rx_val( T0 ),
       .io_rx_rdy( Mapper_io_rx_rdy ),
       .io_tx_dat_int( Mapper_io_tx_dat_int ),
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
  wire[63:0] decode_io_tx_dat_int;
  wire T0;
  wire decode_io_rx_rdy;
  wire Fifo_0_io_deq_val;
  wire[63:0] Fifo_0_io_deq_dat;
  wire Controller_io_tx_val;
  wire[63:0] Controller_io_tx_dat_int;
  wire[63:0] encode_io_tx_dat;
  wire encode_io_tx_val;
  wire T1;
  wire Fifo_1_io_deq_val;
  wire[63:0] Fifo_1_io_deq_dat;

  assign T0 = Fifo_0_io_deq_val && decode_io_rx_rdy;
  assign T1 = decode_io_rx_rdy && Fifo_0_io_deq_val;
  assign io_deq_val = Fifo_1_io_deq_val;
  assign io_deq_dat = Fifo_1_io_deq_dat;
  Fifo_0 Fifo_0(.clk(clk), .reset(reset),
       .io_enq_val( io_enq_val ),
       //.io_enq_rdy(  )
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
       .io_tx_val( decode_io_tx_val )
  );
  Controller Controller(.clk(clk), .reset(reset),
       .io_rx_dat_int( decode_io_tx_dat_int ),
       .io_rx_val( decode_io_tx_val ),
       .io_tx_dat_int( Controller_io_tx_dat_int ),
       .io_tx_val( Controller_io_tx_val )
  );
endmodule

