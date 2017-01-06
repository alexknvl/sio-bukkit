package sio.bukkit.core

import java.net.InetAddress

import sio.core._

package object events {
  type BukkitEvent                  = org.bukkit.event.Event
  type BukkitPluginEnable           = org.bukkit.event.server.PluginEnableEvent
  type BukkitPluginDisable          = org.bukkit.event.server.PluginDisableEvent
  type BukkitServerListPing         = org.bukkit.event.server.ServerListPingEvent
  type BukkitPlayerLogin            = org.bukkit.event.player.PlayerLoginEvent
  type BukkitPlayerQuit             = org.bukkit.event.player.PlayerQuitEvent
  type BukkitPlayerInteract         = org.bukkit.event.player.PlayerInteractEvent
  type BukkitPlayerInteractEntity   = org.bukkit.event.player.PlayerInteractEntityEvent
  type BukkitPlayerInteractAtEntity = org.bukkit.event.player.PlayerInteractAtEntityEvent

  type PluginEnable = IOMutable[BukkitPluginEnable]
  type PluginDisable = IOMutable[BukkitPluginDisable]
  type ServerListPing = IOMutable[BukkitServerListPing]
  type PlayerLogin = IOMutable[BukkitPlayerLogin]

  object PluginEnable {
    implicit class Ops(handle: PluginEnable) extends Mutable.Syntax(handle) {
      def plugin: Plugin = unsafePure { e => IO.mutable(e.getPlugin) }
    }
  }
  object PluginDisable {
    implicit class Ops(handle: PluginDisable) extends Mutable.Syntax(handle) {
      def plugin: Plugin = unsafePure { e => IO.mutable(e.getPlugin) }
    }
  }

  object ServerListPing {
    implicit class Ops(handle: ServerListPing) extends Mutable.Syntax(handle) {
      def getMOTD: IO[String] = lift { _.getMotd }
      def getMaxPlayers: IO[Int] = lift { _.getMaxPlayers }
    }
  }

  object PlayerLogin {
    sealed abstract class Result extends Product with Serializable
    object Result {
      case object Allowed extends Result
      case class KickBanned(message: String) extends Result
      case class KickFull(message: String) extends Result
      case class KickWhitelist(message: String) extends Result
      case class KickOther(message: String) extends Result
    }

    implicit class Ops(handle: PlayerLogin) extends Mutable.Syntax(handle) {
      def address: InetAddress = unsafePure { _.getAddress }
      def hostName: String = unsafePure { _.getHostname }
    }

    implicit val playerLoginEvent: Event[PlayerLogin] = Event.defaultImpl
  }
}
