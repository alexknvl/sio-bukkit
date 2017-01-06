package sio.bukkit.core

import sio.core._

object ItemStack {
  implicit final class Ops(handle: ItemStack) extends Mutable.Syntax(handle) {
    def getAmount: IO[Int] = lift { _.getAmount }
    def setAmount(x: Int): IO[Unit] = lift { _.setAmount(x) }
  }
}
