package me.fixpoint.aicontest2011
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.io.BufferedReader
import java.io.PrintStream
import java.io.InputStreamReader

object Util {
	def thread(name: String, runnable: => Unit) {
		new Thread(name) { override def run = runnable } start
	}

	def copyLines(in: InputStream, out: OutputStream) {
		copyLines(in, new PrintStream(out))
	}

	def copyLines(in: InputStream, out: PrintStream) {
		copyLines(new BufferedReader(new InputStreamReader(in)), out)
	}

	def copyLines(in: BufferedReader, out: PrintStream) {
		copyLines(() => in readLine, (s: String) => { out println s; out flush })
	}

	def copyLines(in: InputStream, send: String => Unit) {
		copyLines(new BufferedReader(new InputStreamReader(in)), send)
	}

	def copyLines(in: BufferedReader, send: String => Unit) {
		copyLines(() => in readLine, send)
	}

	def copyLines(read: () => String, send: String => Unit) {
		while (true) {
			val line = read()

			if (line == null) {
				return
			}

			send(line)
		}
	}

	def copy(in: InputStream, out: OutputStream) {
		try {
			try {
				val buffer = new Array[Byte](1024)
				var count = -1
				while (true) {
					count = in read buffer
					if (count == -1) {
						return
					} else {
						out write (buffer, 0, count)
						out flush
					}
				}
			} finally {
				out.close()
			}
		} finally {
			in.close()
		}
	}

	def link(latch: CountDownLatch, in: InputStream, out: OutputStream) {
		new Thread() {
			override def run {
				try {
					copy(in, out)
				} finally {
					if (latch != null) {
						latch countDown
					}
				}
			}
		} start
	}
}