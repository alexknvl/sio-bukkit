package sio.bukkit.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import org.bukkit.Bukkit
import sio.core._

object Scheduler {
  def isPrimaryThread: IO[Boolean] = IO { Bukkit.isPrimaryThread }
  def cancelAllTasks: IO[Unit] = IO { Bukkit.getScheduler.cancelAllTasks() }
  def cancelTask(taskId: Int): IO[Unit] = IO { Bukkit.getScheduler.cancelTask(taskId) }
  def cancelTasks(plugin: Plugin): IO[Unit] = IO { Bukkit.getScheduler.cancelTasks(plugin.unsafeValue) }
  def isCurrentlyRunning(taskId: Int): IO[Boolean] = IO { Bukkit.getScheduler.isCurrentlyRunning(taskId) }
  def isQueued(taskId: Int): IO[Boolean] = IO { Bukkit.getScheduler.isQueued(taskId) }

  def runTask(plugin: Plugin, action: IO[Unit]): IO[Task] =
    action.asRunnable.liftMap(r => IO.mutable(Bukkit.getScheduler.runTask(plugin.unsafeValue, r)))

  def runTaskLater(plugin: Plugin, action: IO[Unit], delay: Long Refined Positive): IO[Task] =
    action.asRunnable.liftMap(r => IO.mutable(Bukkit.getScheduler.runTaskLater(plugin.unsafeValue, r, delay.value)))

  def runTaskTimer(plugin: Plugin, action: IO[Unit], delay: Long Refined Positive, period: Long Refined Positive): IO[Task] =
    action.asRunnable.liftMap(r => IO.mutable(Bukkit.getScheduler.runTaskTimer(plugin.unsafeValue, r, delay.value, period.value)))
}
