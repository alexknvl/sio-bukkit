package sio.bukkit.core

import java.awt.image.BufferedImage
import java.io.File
import java.net.{InetAddress, InetSocketAddress}

import scala.collection.JavaConverters._

import org.bukkit.Bukkit
import sio.core._

object Server {
  final case class ServerVersion(name: String, version: String, bukkitVersion: String)

  val shutdown: IO[Unit] = IO { Bukkit.shutdown() }
  val reload: IO[Unit] = IO { Bukkit.reload() }

  val getServerVersion: IO[ServerVersion] =
    IO { ServerVersion(Bukkit.getName, Bukkit.getVersion, Bukkit.getBukkitVersion) }
  val getServerAddress: IO[InetSocketAddress] =
    IO { new InetSocketAddress(Bukkit.getIp, Bukkit.getPort) }

  val getConnectionThrottle: IO[Long] = IO { Bukkit.getConnectionThrottle }
  val doGenerateStructures: IO[Boolean] = IO { Bukkit.getGenerateStructures }

  val isOnlineMode: IO[Boolean] = IO { Bukkit.getOnlineMode }
  val isEndAllowed: IO[Boolean] = IO { Bukkit.getAllowEnd }
  val isNetherAllowed: IO[Boolean] = IO { Bukkit.getAllowNether }
  val isFlightAllowed: IO[Boolean] = IO { Bukkit.getAllowFlight }
  val isHardcore: IO[Boolean] = IO { Bukkit.isHardcore }

  val getHostileMobSpawnLimit: IO[Int] = IO { Bukkit.getAmbientSpawnLimit }
  val getAnimalSpawnLimit: IO[Int] = IO { Bukkit.getAnimalSpawnLimit }

  def getConsoleSender: IO[CommandSender] = IO { IO.mutable(Bukkit.getConsoleSender) }
  def loadServerIconFromFile(file: File): IO[CachedServerIcon] = IO { IO.mutable(Bukkit.loadServerIcon(file)) }
  def loadServerIcon(image: IOMutable[BufferedImage]): IO[CachedServerIcon] = IO { IO.mutable(Bukkit.loadServerIcon(image.unsafeValue)) }

  def getPlayerByID(id: Player.ID): IO[Option[Player]] =
    IO { Option(Bukkit.getPlayer(id.value)).map(IO.mutable) }
  def getWorldByID(id: World.ID): IO[Option[World]] =
    IO { Option(Bukkit.getWorld(id.value)).map(IO.mutable) }

  def banIP(address: InetAddress): IO[Unit] = IO { Bukkit.banIP(address.getHostAddress) }
  def unbanIP(address: InetAddress): IO[Unit] = IO { Bukkit.unbanIP(address.getHostAddress) }

  def broadcast(message: String, permission: Permission): IO[Unit] = IO { Bukkit.broadcast(message, permission.name) }
  def broadcastMessage(message: String): IO[Unit] = IO { Bukkit.broadcastMessage(message) }

  val isWhitelisted: IO[Boolean] = IO { Bukkit.hasWhitelist }
  val reloadWhitelist: IO[Unit] = IO { Bukkit.reloadWhitelist() }
  def setWhitelisted(value: Boolean): IO[Unit] = IO { Bukkit.setWhitelist(value) }
  val getWhitelistedPlayers: IO[List[OfflinePlayer]] = IO { Bukkit.getWhitelistedPlayers.asScala.toList.map(IO.mutable) }
}