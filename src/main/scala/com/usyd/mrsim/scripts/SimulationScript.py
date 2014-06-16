#!/usr/bin/env python


from tempfile import mkstemp
from shutil import move
from os import remove, close, environ, getcwd
import subprocess
import sys

def replace(file_path, pattern, subst):
    #Create temp file
    fh, abs_path = mkstemp()
    new_file = open(abs_path,'w')
    old_file = open(file_path)
    text = old_file.read()
    new_file.write(text.replace(pattern, subst))
    #close temp file
    new_file.close()
    close(fh)
    old_file.close()
    #Remove original file
    remove(file_path)
    #Move new file
    move(abs_path, file_path)

if __name__ == "__main__":

  EMU_OLD = """#include <stdexcept>"""

  EMU_NEW = """#include <stdexcept>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <ctype.h>
#include <dirent.h>

#define IN_FIFO "/tmp/hw_in_pipe"
#define OUT_FIFO "/tmp/hw_out_pipe"
#define FLAGOUT_NAME "/tmp/flagOut.lock"
#define SIZE 16"""

  EMU_OLD2 = """void read_eval_print (FILE *f, FILE *teefile = NULL) {
    timestep = 0;
    for (;;) {
      std::string str_in;
      getline(cin,str_in);
      if (teefile != NULL) {
          fprintf(teefile, "%s\\n", str_in.c_str());
          fflush(teefile);
      }
      if (strcmp("", str_in.c_str()) == 0) {
          fprintf(stderr, "Read empty string in tester stdin\\n");
          abort();
      }
      std::vector< std::string > tokens = tokenize(str_in);
      std::string cmd = tokens[0];
      if (cmd == "peek") {
        std::string res;
        if (tokens.size() == 2) {
          res = node_read(tokens[1]);
        } else if (tokens.size() == 3) {
          res = mem_read(tokens[1], tokens[2]);
        }
        // fprintf(stderr, "-PEEK %s -> %s\\n", tokens[1].c_str(), res.c_str());
        cout << res << endl;
      } else if (cmd == "poke") {
        bool res;
        // fprintf(stderr, "-POKE %s <- %s\\n", tokens[1].c_str(), tokens[2].c_str());
        if (tokens.size() == 3)
          res = node_write(tokens[1], tokens[2]);
        else if (tokens.size() == 4)
          res = mem_write(tokens[1], tokens[2], tokens[3]);
      } else if (cmd == "step") {
        int n = atoi(tokens[1].c_str());
        // fprintf(stderr, "-STEP %d\\n", n);
        int new_delta = step(0, n, f, true);
        cout << new_delta << endl;
      } else if (cmd == "reset") {
        int n = atoi(tokens[1].c_str());
        // fprintf(stderr, "-RESET %d\\n", n);
        step(1, n, f, false);
      } else if (cmd == "set-clocks") {
        std::vector< int > periods;
        for (int i = 1; i < tokens.size(); i++) {
          int period = atoi(tokens[i].c_str());
          periods.push_back(period);
        }
        setClocks(periods);
      } else if (cmd == "quit") {
          return;
      } else {
        fprintf(stderr, "Unknown command: |%s|\\n", cmd.c_str());
      }
    }
  }"""

  EMU_NEW2="""
void read_eval_print (FILE *f, FILE *teefile = NULL) {
  int count = 0;
  for (;;) {
  int fd_in = open(IN_FIFO, O_RDONLY | O_NONBLOCK);
    node_write("MrSimSimulation.io_enq_val", "0x0");
    get_val(f, fd_in);

    char *val_read = new char[1];
    strcpy(val_read, node_read("MrSimSimulation.io_deq_val").c_str());
    if ((strcmp(val_read,"0x1") == 0) && (count > 10)) {
      send_val(f);
      count = 0;
    } else {
      node_write("MrSimSimulation.io_deq_rdy", "0x0");
    }
    count++;
    free(val_read);
    int new_delta = step(0, 1, f, true);
  close(fd_in);
  }
}

void get_val(FILE *f, int fd_in) {
  // Read Result
  char *in_res = (char *)malloc(SIZE);
  ssize_t r = 0;
  // Attempt to Read Result
  while(read(fd_in, in_res, SIZE) > 0) {
    node_write("MrSimSimulation.io_enq_val", "0x1");
    node_write("MrSimSimulation.io_enq_dat", "0x" +string(in_res));
    int new_delta = step(0, 1, f, true);
  }
  free(in_res);
}

void send_val(FILE *f) {
  // Write Result
  int fd_out = open(OUT_FIFO, O_WRONLY);
  char *out_res = new char[SIZE];
  node_write("MrSimSimulation.io_deq_rdy", "0x1");
  int new_delta = step(0, 1, f, true);
  strcpy(out_res, node_read("MrSimSimulation.io_deq_dat").c_str());
  write(fd_out, out_res, SIZE);
  close(fd_out);
  free(out_res);
}"""




  replace("emulator.h", EMU_OLD, EMU_NEW)
  replace("emulator.h", EMU_OLD2, EMU_NEW2)
