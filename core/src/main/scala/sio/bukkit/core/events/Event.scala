package sio.bukkit.core.events

import org.bukkit.Bukkit
import org.bukkit.event.{Listener, EventPriority}
import org.bukkit.plugin.EventExecutor
import sio.bukkit.core._
import sio.core._

import scala.reflect.ClassTag

trait Event[E] {
  def listen(plugin: Plugin, priority: EventPriority, ignoreCancelled: Boolean)(f: E => IO[Unit]): IO[Unit]
}
object Event {
  def apply[E](implicit E: Event[E]): Event[E] = E

  def defaultImpl[E <: BukkitEvent](implicit E: ClassTag[E]): Event[IOMutable[E]] = new Event[IOMutable[E]] {
    def listen(plugin: Plugin, priority: EventPriority, ignoreCancelled: Boolean)(run: IOMutable[E] => IO[Unit]): IO[Unit] =
      IO.callback(run).liftMap { f =>
        Bukkit.getPluginManager.registerEvent(E.runtimeClass.asInstanceOf[Class[BukkitEvent]], null, priority, new EventExecutor {
          override def execute(listener: Listener, event: BukkitEvent): Unit =
            f(IO.mutable(event.asInstanceOf[E]))
        }, plugin.unsafeValue, ignoreCancelled)
      }
  }
}
