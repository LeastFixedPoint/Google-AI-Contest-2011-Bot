package me.fixpoint.aicontest2011

import java.io.BufferedWriter
import scala.io.BufferedSource
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import com.weiglewilczek.slf4s.Logging

class AI(val read: () => String, val send: String => Unit) extends Logging {
	var rows = -1
	var cols = -1
	var maxTurns = -1
	var viewRadius = -1
	var attackRadius = -1
	var spawnRadius = -1

	case class Ant(var x: Int, var y: Int)

	case class Tile(
		var water: Option[Boolean] = None,
		var ant: Option[Int] = None,
		var hill: Option[Int] = None,
		var food: Option[Unit] = None,
		var lastSeen: Int = -1)

	var ants: Buffer[Ant] = ArrayBuffer()
	var map: Array[Array[Tile]] = null
	var turn = 0

	def run = {
		val line = read()
		assert(line == "turn 0")
		doSetup
		while (doTurn) {}
	}

	val turnMatchers = List(
		new PrefixInt2("w ", map(_)(_) water = Some(true)),
		new PrefixInt2("f ", map(_)(_) food = Some()),
		new PrefixInt3("h ", (r, c, owner) => { map(r)(c) hill = Some(owner) }),
		new PrefixInt3("a ", (r, c, owner) => { map(r)(c) ant = Some(owner); if (owner == 0) ants += Ant(r, c) }),
		new PrefixInt3("d ", (r, c, owner) => {}),
		Stop("go"))

	def doTurn: Boolean = {
		turn += 1

		val line = read()
		if (line == "end") { logger info "GAME OVER"; return false }
		assert(line == "turn " + turn, "Expected [turn " + turn + "], but got [" + line + "]")

		logwrap("turn " + turn) {
			processLinesWith(turnMatchers)
			send("go")
		}

		true
	}

	def doSetup = logwrap("setup") {
		processLinesWith(List(
			new PrefixInt("rows ", rows = _),
			new PrefixInt("cols ", cols = _),
			new PrefixInt("turns ", maxTurns = _),
			new PrefixInt("viewradius2 ", viewRadius = _),
			new PrefixInt("attackradius2 ", attackRadius = _),
			new PrefixInt("spawnradius2 ", spawnRadius = _),
			Stop("ready")))

		map = Array.fill(rows, cols) { Tile() }

		send("go")
	}

	def processLinesWith(matchers: Seq[Matcher]): Unit = {
		while (true) {
			val line = read()
			matchers find (_ matches line) match {
				case Some(Stop(token)) => logger trace "Stop signal [" + token + "] received"; return
				case Some(matcher) => logger trace "Matcher " + matcher + " matched"
				case None => logger trace "Nothing matched"
			}
		}
	}

	case class Matcher(matches: String => Boolean, action: String => Unit)

	implicit def string2My(s: String) = new MyString(s)

	class MyString(s: String) {
		def substringAfter(prefix: String) =
			s.substring(prefix.length)

		def tokensAfter(prefix: String) =
			substringAfter(prefix).split(" ")

		def hasTokensAfter(prefix: String, n: Int) =
			s.startsWith(prefix) && tokensAfter(prefix).length == n
	}

	class PrefixedTokens(prefix: String, n: Int, action: String => Unit) extends Matcher(
		s => (s startsWith prefix) && (s tokensAfter prefix).length == n, action)

	class PrefixInt(prefix: String, action: Int => Unit) extends PrefixedTokens(
		prefix, 1, s => action(s substringAfter prefix toInt))

	class PrefixInt2(prefix: String, action: (Int, Int) => Unit) extends PrefixedTokens(
		prefix, 2, _.tokensAfter(prefix) match {
			case Array(s1, s2) => action(s1.toInt, s2.toInt)
		})

	class PrefixInt3(prefix: String, action: (Int, Int, Int) => Unit) extends PrefixedTokens(
		prefix, 3, _.tokensAfter(prefix) match {
			case Array(s1, s2, s3) => action(s1.toInt, s2.toInt, s3.toInt)
		})

	class Equals(token: String, action: () => Unit) extends Matcher(_ == token, _ => action())

	case class Stop(endToken: String) extends Equals(endToken, () => {})

	def pretrace[T](msg: String, action: T => Unit)(value: T) = {
		logger trace msg
		action(value)
	}

	def pretrace(msg: String, action: => Unit) = {
		logger trace msg
		action
	}

	def logwrap(name: String)(code: => Unit) {
		logger info "Starting " + name
		code
		logger info "Finished " + name
	}
}