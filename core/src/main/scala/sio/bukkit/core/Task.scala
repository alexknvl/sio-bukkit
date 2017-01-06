package sio.bukkit.core

import sio.core._

object Task {
  implicit class Ops(handle: Task) extends Mutable.Syntax(handle) {
    def owner: Plugin = unsafePure { t => IO.mutable(t.getOwner) }
    def taskId: Int = unsafePure { _.getTaskId }
    def isSync: Boolean = unsafePure { _.isSync }
    def cancel: IO[Unit] = lift { _.cancel() }
  }
}
