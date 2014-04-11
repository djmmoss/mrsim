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
  wire[63:0] T2;
  wire[63:0] T3;
  reg [63:0] ram [3:0];
  wire[63:0] T4;
  wire[63:0] T5;
  wire do_enq;
  wire T6;
  reg[0:0] is_full;
  wire is_full_next;
  wire T7;
  wire T8;
  wire do_deq;
  wire T9;
  wire is_empty;
  wire T10;
  reg[3:0] deq_ptr;
  wire[3:0] T11;
  wire[3:0] deq_ptr_inc;
  reg[3:0] enq_ptr;
  wire[3:0] T12;
  wire[3:0] enq_ptr_inc;
  wire T13;
  wire T14;
  wire T15;
  wire T16;
  wire T17;
  wire[1:0] T18;
  wire[1:0] T19;
  wire T20;
  wire T21;

  assign io_deq_dat_int = T0;
  assign T0 = T1[6'h3f:1'h0];
  assign T1 = do_deq ? T2 : 64'h0;
  assign T2 = T3[6'h3f:1'h0];
  assign T3 = ram[T19];
  assign T5 = io_enq_dat_int;
  assign do_enq = T6 && io_enq_val;
  assign T6 = ! is_full;
  assign is_full_next = T14 ? 1'h1 : T7;
  assign T7 = T8 ? 1'h0 : is_full;
  assign T8 = do_deq && is_full;
  assign do_deq = io_deq_rdy && T9;
  assign T9 = ! is_empty;
  assign is_empty = T13 && T10;
  assign T10 = enq_ptr == deq_ptr;
  assign T11 = do_deq ? deq_ptr_inc : deq_ptr;
  assign deq_ptr_inc = deq_ptr + 4'h1;
  assign T12 = do_enq ? enq_ptr_inc : enq_ptr;
  assign enq_ptr_inc = enq_ptr + 4'h1;
  assign T13 = ! is_full;
  assign T14 = T16 && T15;
  assign T15 = enq_ptr_inc == deq_ptr;
  assign T16 = do_enq && T17;
  assign T17 = ~ do_deq;
  assign T18 = enq_ptr[1'h1:1'h0];
  assign T19 = deq_ptr[1'h1:1'h0];
  assign io_deq_val = T20;
  assign T20 = ! is_empty;
  assign io_enq_rdy = T21;
  assign T21 = ! is_full;

  always @(posedge clk) begin
    if (do_enq)
      ram[T18] <= T5;
    is_full <= reset ? 1'h0 : is_full_next;
    deq_ptr <= reset ? 4'h0 : T11;
    enq_ptr <= reset ? 4'h0 : T12;
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
  wire Fifo_3_io_deq_val;
  wire Mapper_3_io_rx_rdy;
  wire T2;
  wire[63:0] Fifo_3_io_deq_dat_int;
  wire T3;
  wire T4;
  wire Fifo_2_io_deq_val;
  wire Mapper_2_io_rx_rdy;
  wire T5;
  wire[63:0] Fifo_2_io_deq_dat_int;
  wire T6;
  wire T7;
  wire Fifo_1_io_deq_val;
  wire Mapper_1_io_rx_rdy;
  wire T8;
  wire[63:0] Fifo_1_io_deq_dat_int;
  wire T9;
  wire T10;
  wire Fifo_0_io_deq_val;
  wire Mapper_0_io_rx_rdy;
  wire T11;
  wire[63:0] Fifo_0_io_deq_dat_int;
  wire[63:0] Mapper_3_io_tx_dat_int;
  wire T12;
  wire T13;
  wire Fifo_7_io_deq_val;
  wire T14;
  wire[1:0] index;
  wire[1:0] T15;
  wire[1:0] T16;
  wire T17;
  wire f_out_deq_rdy_1;
  wire T18;
  wire Fifo_5_io_deq_val;
  wire T19;
  wire f_out_deq_rdy_2;
  wire T20;
  wire Fifo_6_io_deq_val;
  wire T21;
  wire f_out_deq_rdy_3;
  wire T22;
  wire T23;
  wire T24;
  wire T25;
  wire Fifo_7_io_enq_rdy;
  wire Mapper_3_io_tx_val;
  wire T26;
  wire[63:0] Mapper_2_io_tx_dat_int;
  wire T27;
  wire T28;
  wire T29;
  wire T30;
  wire T31;
  wire T32;
  wire Fifo_6_io_enq_rdy;
  wire Mapper_2_io_tx_val;
  wire T33;
  wire[63:0] Mapper_1_io_tx_dat_int;
  wire T34;
  wire T35;
  wire T36;
  wire T37;
  wire T38;
  wire T39;
  wire Fifo_5_io_enq_rdy;
  wire Mapper_1_io_tx_val;
  wire T40;
  wire[63:0] Mapper_0_io_tx_dat_int;
  wire T41;
  wire T42;
  wire Fifo_4_io_deq_val;
  wire T43;
  wire T44;
  wire T45;
  wire T46;
  wire Fifo_4_io_enq_rdy;
  wire Mapper_0_io_tx_val;
  wire T47;
  wire T48;
  wire T49;
  wire T50;
  wire Fifo_3_io_enq_rdy;
  wire T51;
  wire T52;
  reg[2:0] inCounter;
  wire T53;
  wire T54;
  wire T55;
  wire Fifo_2_io_enq_rdy;
  wire T56;
  wire T57;
  wire T58;
  wire T59;
  wire Fifo_1_io_enq_rdy;
  wire T60;
  wire T61;
  wire T62;
  wire Fifo_0_io_enq_rdy;
  wire T63;
  wire T64;
  wire[2:0] T65;
  wire[2:0] T66;
  wire[2:0] T67;
  wire[2:0] T68;
  wire[2:0] T69;
  wire T70;
  wire[2:0] T71;
  wire[2:0] T72;
  wire T73;
  wire[2:0] T74;
  wire[2:0] T75;
  wire T76;
  wire[2:0] T77;
  wire[2:0] T78;
  wire T79;
  wire T80;
  wire T81;
  wire T82;
  wire T83;
  wire T84;
  wire T85;
  wire T86;
  wire T87;
  wire T88;
  wire T89;
  wire T90;
  wire T91;
  wire T92;
  wire[63:0] T93;
  wire[63:0] T94;
  wire[63:0] T95;
  wire[63:0] T96;
  wire[63:0] Fifo_4_io_deq_dat_int;
  wire[63:0] Fifo_5_io_deq_dat_int;
  wire[63:0] Fifo_6_io_deq_dat_int;
  wire[63:0] Fifo_7_io_deq_dat_int;

  assign T0 = T2 ? 1'h0 : T1;
  assign T1 = Mapper_3_io_rx_rdy && Fifo_3_io_deq_val;
  assign T2 = ! T1;
  assign T3 = T5 ? 1'h0 : T4;
  assign T4 = Mapper_2_io_rx_rdy && Fifo_2_io_deq_val;
  assign T5 = ! T4;
  assign T6 = T8 ? 1'h0 : T7;
  assign T7 = Mapper_1_io_rx_rdy && Fifo_1_io_deq_val;
  assign T8 = ! T7;
  assign T9 = T11 ? 1'h0 : T10;
  assign T10 = Mapper_0_io_rx_rdy && Fifo_0_io_deq_val;
  assign T11 = ! T10;
  assign T12 = T23 ? 1'h0 : T13;
  assign T13 = T14 && Fifo_7_io_deq_val;
  assign T14 = 2'h3 == index;
  assign index = T21 ? 2'h3 : T15;
  assign T15 = T19 ? 2'h2 : T16;
  assign T16 = {1'h0, T17};
  assign T17 = f_out_deq_rdy_1 == 1'h1;
  assign f_out_deq_rdy_1 = T18;
  assign T18 = Fifo_5_io_deq_val;
  assign T19 = f_out_deq_rdy_2 == 1'h1;
  assign f_out_deq_rdy_2 = T20;
  assign T20 = Fifo_6_io_deq_val;
  assign T21 = f_out_deq_rdy_3 == 1'h1;
  assign f_out_deq_rdy_3 = T22;
  assign T22 = Fifo_7_io_deq_val;
  assign T23 = ! T13;
  assign T24 = T26 ? 1'h0 : T25;
  assign T25 = Mapper_3_io_tx_val && Fifo_7_io_enq_rdy;
  assign T26 = ! T25;
  assign T27 = T30 ? 1'h0 : T28;
  assign T28 = T29 && Fifo_6_io_deq_val;
  assign T29 = 2'h2 == index;
  assign T30 = ! T28;
  assign T31 = T33 ? 1'h0 : T32;
  assign T32 = Mapper_2_io_tx_val && Fifo_6_io_enq_rdy;
  assign T33 = ! T32;
  assign T34 = T37 ? 1'h0 : T35;
  assign T35 = T36 && Fifo_5_io_deq_val;
  assign T36 = 2'h1 == index;
  assign T37 = ! T35;
  assign T38 = T40 ? 1'h0 : T39;
  assign T39 = Mapper_1_io_tx_val && Fifo_5_io_enq_rdy;
  assign T40 = ! T39;
  assign T41 = T44 ? 1'h0 : T42;
  assign T42 = T43 && Fifo_4_io_deq_val;
  assign T43 = 2'h0 == index;
  assign T44 = ! T42;
  assign T45 = T47 ? 1'h0 : T46;
  assign T46 = Mapper_0_io_tx_val && Fifo_4_io_enq_rdy;
  assign T47 = ! T46;
  assign T48 = T2 ? 1'h0 : T1;
  assign T49 = T80 ? 1'h0 : T50;
  assign T50 = T51 && Fifo_3_io_enq_rdy;
  assign T51 = T52 && io_rx_val;
  assign T52 = 3'h3 == inCounter;
  assign T53 = T54 || T50;
  assign T54 = T58 || T55;
  assign T55 = T56 && Fifo_2_io_enq_rdy;
  assign T56 = T57 && io_rx_val;
  assign T57 = 3'h2 == inCounter;
  assign T58 = T62 || T59;
  assign T59 = T60 && Fifo_1_io_enq_rdy;
  assign T60 = T61 && io_rx_val;
  assign T61 = 3'h1 == inCounter;
  assign T62 = T63 && Fifo_0_io_enq_rdy;
  assign T63 = T64 && io_rx_val;
  assign T64 = 3'h0 == inCounter;
  assign T65 = T50 ? T77 : T66;
  assign T66 = T55 ? T74 : T67;
  assign T67 = T59 ? T71 : T68;
  assign T68 = T70 ? 3'h0 : T69;
  assign T69 = inCounter + 3'h1;
  assign T70 = inCounter == 3'h3;
  assign T71 = T73 ? 3'h0 : T72;
  assign T72 = inCounter + 3'h1;
  assign T73 = inCounter == 3'h3;
  assign T74 = T76 ? 3'h0 : T75;
  assign T75 = inCounter + 3'h1;
  assign T76 = inCounter == 3'h3;
  assign T77 = T79 ? 3'h0 : T78;
  assign T78 = inCounter + 3'h1;
  assign T79 = inCounter == 3'h3;
  assign T80 = ! T50;
  assign T81 = T5 ? 1'h0 : T4;
  assign T82 = T83 ? 1'h0 : T55;
  assign T83 = ! T55;
  assign T84 = T8 ? 1'h0 : T7;
  assign T85 = T86 ? 1'h0 : T59;
  assign T86 = ! T59;
  assign T87 = T11 ? 1'h0 : T10;
  assign T88 = T89 ? 1'h0 : T62;
  assign T89 = ! T62;
  assign io_tx_val = T90;
  assign T90 = T13 ? 1'h1 : T91;
  assign T91 = T28 ? 1'h1 : T92;
  assign T92 = T35 ? 1'h1 : T42;
  assign io_tx_dat_int = T93;
  assign T93 = T13 ? Fifo_7_io_deq_dat_int : T94;
  assign T94 = T28 ? Fifo_6_io_deq_dat_int : T95;
  assign T95 = T35 ? Fifo_5_io_deq_dat_int : T96;
  assign T96 = T42 ? Fifo_4_io_deq_dat_int : 64'h0;
  Fifo_1 Fifo_0(.clk(clk), .reset(reset),
       .io_enq_val( T88 ),
       .io_enq_rdy( Fifo_0_io_enq_rdy ),
       .io_deq_val( Fifo_0_io_deq_val ),
       .io_deq_rdy( T87 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_deq_dat_int( Fifo_0_io_deq_dat_int )
  );
  Fifo_1 Fifo_1(.clk(clk), .reset(reset),
       .io_enq_val( T85 ),
       .io_enq_rdy( Fifo_1_io_enq_rdy ),
       .io_deq_val( Fifo_1_io_deq_val ),
       .io_deq_rdy( T84 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_deq_dat_int( Fifo_1_io_deq_dat_int )
  );
  Fifo_1 Fifo_2(.clk(clk), .reset(reset),
       .io_enq_val( T82 ),
       .io_enq_rdy( Fifo_2_io_enq_rdy ),
       .io_deq_val( Fifo_2_io_deq_val ),
       .io_deq_rdy( T81 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_deq_dat_int( Fifo_2_io_deq_dat_int )
  );
  Fifo_1 Fifo_3(.clk(clk), .reset(reset),
       .io_enq_val( T49 ),
       .io_enq_rdy( Fifo_3_io_enq_rdy ),
       .io_deq_val( Fifo_3_io_deq_val ),
       .io_deq_rdy( T48 ),
       .io_enq_dat_int( io_rx_dat_int ),
       .io_deq_dat_int( Fifo_3_io_deq_dat_int )
  );
  Fifo_1 Fifo_4(.clk(clk), .reset(reset),
       .io_enq_val( T45 ),
       .io_enq_rdy( Fifo_4_io_enq_rdy ),
       .io_deq_val( Fifo_4_io_deq_val ),
       .io_deq_rdy( T41 ),
       .io_enq_dat_int( Mapper_0_io_tx_dat_int ),
       .io_deq_dat_int( Fifo_4_io_deq_dat_int )
  );
  Fifo_1 Fifo_5(.clk(clk), .reset(reset),
       .io_enq_val( T38 ),
       .io_enq_rdy( Fifo_5_io_enq_rdy ),
       .io_deq_val( Fifo_5_io_deq_val ),
       .io_deq_rdy( T34 ),
       .io_enq_dat_int( Mapper_1_io_tx_dat_int ),
       .io_deq_dat_int( Fifo_5_io_deq_dat_int )
  );
  Fifo_1 Fifo_6(.clk(clk), .reset(reset),
       .io_enq_val( T31 ),
       .io_enq_rdy( Fifo_6_io_enq_rdy ),
       .io_deq_val( Fifo_6_io_deq_val ),
       .io_deq_rdy( T27 ),
       .io_enq_dat_int( Mapper_2_io_tx_dat_int ),
       .io_deq_dat_int( Fifo_6_io_deq_dat_int )
  );
  Fifo_1 Fifo_7(.clk(clk), .reset(reset),
       .io_enq_val( T24 ),
       .io_enq_rdy( Fifo_7_io_enq_rdy ),
       .io_deq_val( Fifo_7_io_deq_val ),
       .io_deq_rdy( T12 ),
       .io_enq_dat_int( Mapper_3_io_tx_dat_int ),
       .io_deq_dat_int( Fifo_7_io_deq_dat_int )
  );
  Mapper Mapper_0(
       .io_rx_dat_int( Fifo_0_io_deq_dat_int ),
       .io_rx_val( T9 ),
       .io_rx_rdy( Mapper_0_io_rx_rdy ),
       .io_tx_dat_int( Mapper_0_io_tx_dat_int ),
       .io_tx_val( Mapper_0_io_tx_val )
  );
  Mapper Mapper_1(
       .io_rx_dat_int( Fifo_1_io_deq_dat_int ),
       .io_rx_val( T6 ),
       .io_rx_rdy( Mapper_1_io_rx_rdy ),
       .io_tx_dat_int( Mapper_1_io_tx_dat_int ),
       .io_tx_val( Mapper_1_io_tx_val )
  );
  Mapper Mapper_2(
       .io_rx_dat_int( Fifo_2_io_deq_dat_int ),
       .io_rx_val( T3 ),
       .io_rx_rdy( Mapper_2_io_rx_rdy ),
       .io_tx_dat_int( Mapper_2_io_tx_dat_int ),
       .io_tx_val( Mapper_2_io_tx_val )
  );
  Mapper Mapper_3(
       .io_rx_dat_int( Fifo_3_io_deq_dat_int ),
       .io_rx_val( T0 ),
       .io_rx_rdy( Mapper_3_io_rx_rdy ),
       .io_tx_dat_int( Mapper_3_io_tx_dat_int ),
       .io_tx_val( Mapper_3_io_tx_val )
  );

  always @(posedge clk) begin
    if(reset) begin
      inCounter <= 3'h0;
    end else if(T53) begin
      inCounter <= T65;
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

