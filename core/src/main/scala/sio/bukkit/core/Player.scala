package sio.bukkit.core

import java.net.InetSocketAddress
import java.util.UUID

import org.bukkit.Location
import sio.core._

object Player {
  final case class ID(value: UUID) extends AnyVal

  implicit class Ops(handle: Player) extends Mutable.Syntax(handle) {
    def getDisplayName: IO[String] = lift { _.getDisplayName }
    def setDisplayName(s: String): IO[Unit] = lift { _.setDisplayName(s) }

    def getPlayerListName: IO[String] = lift { _.getPlayerListName }
    def setPlayerListName(s: String): IO[Unit] = lift { _.setPlayerListName(s) }

    def getCompassTarget: IO[Position] = lift { p =>
      val loc = p.getCompassTarget
      Position(loc.getX, loc.getY, loc.getZ)
    }
    def setCompassTarget(position: Position): IO[Unit] = lift { p =>
      p.setCompassTarget(new Location(p.getWorld, position.x, position.y, position.z))
    }

    def getAddress: IO[InetSocketAddress] = lift { _.getAddress }

    def kick(reason: String): IO[Unit] = lift { _.kickPlayer(reason) }
  }
}