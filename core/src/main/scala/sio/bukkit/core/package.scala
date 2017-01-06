package sio.bukkit

import sio.core._

package object core {
  type BukkitItemStack        = org.bukkit.inventory.ItemStack
  type BukkitCachedServerIcon = org.bukkit.util.CachedServerIcon
  type BukkitInventory        = org.bukkit.inventory.Inventory
  type BukkitCommand          = org.bukkit.command.Command
  type BukkitCommandSender    = org.bukkit.command.CommandSender
  type BukkitTask             = org.bukkit.scheduler.BukkitTask
  type BukkitPlugin           = org.bukkit.plugin.Plugin
  type BukkitPlayer           = org.bukkit.entity.Player
  type BukkitOfflinePlayer    = org.bukkit.OfflinePlayer
  type BukkitWorld            = org.bukkit.World

  type ItemStack        = IOMutable[BukkitItemStack]
  type CachedServerIcon = IOMutable[BukkitCachedServerIcon]
  type Inventory        = IOMutable[BukkitInventory]
  type CommandSender    = IOMutable[BukkitCommandSender]
  type Command          = IOMutable[BukkitCommand]
  type Task             = IOMutable[BukkitTask]
  type Plugin           = IOMutable[BukkitPlugin]
  type Player           = IOMutable[BukkitPlayer]
  type OfflinePlayer    = IOMutable[BukkitOfflinePlayer]
  type World            = IOMutable[BukkitWorld]

  final case class Position(x: Double, y: Double, z: Double)
  final case class BlockPosition(x: Int, y: Int, z: Int)
  final case class Permission(name: String) extends AnyVal
}
