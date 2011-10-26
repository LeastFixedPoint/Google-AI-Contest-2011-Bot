package me.fixpoint.aicontest2011
import java.net.Socket
import java.net.ServerSocket
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import com.weiglewilczek.slf4s.Logging
import java.io.File
import java.io.BufferedReader
import java.io.PrintStream
import java.io.InputStreamReader

object App extends scala.App with Logging {
	logger info "Current directory: " + new File("").getAbsolutePath()

	new Thread("ai") {
		override def run {
			logger info "Waiting for connection from bot"

			val socket = new ServerSocket(3000)
			val connection = socket.accept

			logger info "Connection with bot established, starting AI"

			val sin = new BufferedReader(new InputStreamReader(connection.getInputStream()))
			val sout = new PrintStream(connection.getOutputStream())

			val in = () => { val line = sin readLine; logger trace "=> " + line; line }
			val out = (s: String) => { logger trace "<= " + s; sout println s }

			new AI(in, out) run

			logger info "AI terminated"

			socket close

			logger info "Socket closed"
		}
	} start

	logger info "Launching game"

	val runtime = Runtime.getRuntime()
	val botLine = "java -cp ../bin;%SCALA_HOME%/lib/scala-library.jar;../lib/* me.fixpoint.aicontest2011.Bot"
	//	val process = runtime.exec(Array("cmd", "/c", "play_one_game.cmd", botLine), null, new File("tools"))
	val process = runtime.exec(Array("cmd", "/c", "test_bot.cmd", botLine), null, new File("tools"))

	logger info "Game process launched"

	Util.thread("game", Util.copyLines(process.getInputStream(), (s: String) => logger debug s))

	process.waitFor

	logger info "Game process terminated"
}