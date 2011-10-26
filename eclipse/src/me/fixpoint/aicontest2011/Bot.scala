package me.fixpoint.aicontest2011
import scala.io.BufferedSource
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import com.weiglewilczek.slf4s.Logging
import java.net.Socket
import java.io.BufferedInputStream
import java.io.PrintStream
import java.util.concurrent.CountDownLatch

object Bot extends scala.App {
	val socket = new Socket("localhost", 3000)
	val sin = new BufferedReader(new InputStreamReader(socket.getInputStream()))
	val sout = new PrintStream(socket.getOutputStream())

	val in = () => sin readLine
	val out = (s: String) => sout println s

	Util.thread("socket->out", Util.copyLines(socket.getInputStream(), System.out))
	Util.thread("in->socket", Util.copyLines(System.in, socket.getOutputStream()))
}
