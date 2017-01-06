package sio.bukkit.core

import sio.core._

object Command {
  implicit class CommandOps(handle: Command) extends Mutable.Syntax(handle) {
    def getName: IO[String] = lift { _.getName }
    def setName(name: String): IO[Boolean] = lift { _.setName(name) }
  }
}
