package sio.bukkit.core

import sio.core._

object CommandSender {
  implicit class Ops(handle: CommandSender) extends Mutable.Syntax(handle) {
    def sendMessage(s: String): IO[Unit] = lift { _.sendMessage(s) }
  }
}
