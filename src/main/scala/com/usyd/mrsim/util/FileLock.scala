package com.usyd.mrsim.util;

import java.io._
import java.nio.file._

class FileLock(val file: File) {
    private val channel = new RandomAccessFile(file, "rw").getChannel()
    private var flock: java.nio.channels.FileLock = null
    
    /**
     * Lock the file or throw an exception if the lock is already held
     */
    def lock() {
      this synchronized {
        flock = channel.lock()
      }
    }
    
    /**
     * Try to lock the file and return true if the locking succeeds
     */
    def tryLock(): Boolean = {
      this synchronized {
        try {
          // weirdly this method will return null if the lock is held by another
          // process, but will throw an exception if the lock is held by this process
          // so we have to handle both cases
          flock = channel.tryLock()
          flock != null
        } catch {
          case e: IOException => false
        }
      }
    }
    
    /**
     * Unlock the lock if it is held
     */
    def unlock() {
      this synchronized {
        if(flock != null)
          flock.release()
      }
    }
    
    /**
     * Destroy this lock, closing the associated FileChannel
     */
    def destroy() = {
      this synchronized {
        unlock()
        channel.close()
      }
    }
}