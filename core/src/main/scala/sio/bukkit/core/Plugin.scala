package sio.bukkit.core

import java.io.File

import scala.collection.JavaConverters._

import cats.data.StateT
import org.bukkit.Bukkit
import sio.core._
import sio.core.instances.io._

object Plugin {
  implicit class Ops(handle: Plugin) extends Mutable.Syntax(handle) {
    def name: Plugin.Name = unsafePure { p => Plugin.Name(p.getName) }
    def dataFolder: File = unsafePure { _.getDataFolder }
    def enabled: IO[Boolean] = lift { _.isEnabled }
  }

  final case class Name(value: String) extends AnyVal

  val clearPlugins: IO[Unit] =
    IO { Bukkit.getPluginManager.clearPlugins() }
  val disablePlugins: IO[Unit] =
    IO { Bukkit.getPluginManager.disablePlugins() }
  def disablePlugin(plugin: Plugin): IO[Unit] =
    IO { Bukkit.getPluginManager.disablePlugin(plugin.unsafeValue) }
  def enablePlugin(plugin: Plugin): IO[Unit] =
    IO { Bukkit.getPluginManager.enablePlugin(plugin.unsafeValue) }
  def getPlugin(name: Plugin.Name): IO[Option[Plugin]] =
    IO { Option(Bukkit.getPluginManager.getPlugin(name.value)).map(IO.mutable) }
  def getPlugins: IO[List[Plugin]] =
    IO { Bukkit.getPluginManager.getPlugins.toList.map(IO.mutable) }
  def loadPlugin(file: File) =
    IO { Bukkit.getPluginManager.loadPlugin(file) }

  trait Safe[S] {
    def enable: IO[S]

    def disable(state: S): IO[Unit] =
      IO.unit
    def onCommand(commandSender: CommandSender, command: Command, alias: String, args: List[String]): StateT[IO, S, Boolean] =
      StateT.lift[IO, S, Boolean](IO.pure(false))
    def onTabComplete(commandSender: CommandSender, command: Command, alias: String, args: List[String]): StateT[IO, S, List[String]] =
      StateT.lift[IO, S, List[String]](IO.pure(Nil))
  }

  abstract class Impl[S](base: Safe[S]) extends org.bukkit.plugin.java.JavaPlugin {
    private var state: Option[S] = None

    @inline private def runState[A](f: StateT[IO, S, A]): A = {
      val currentState: S = state.getOrElse(throw new IllegalStateException("State is not defined."))
      val (newState, handled) = IO.unsafeRun(f.run(currentState))
      state = Some(newState)
      handled
    }

    override def onEnable(): Unit = {
      System.err.println("onEnable")
      val action = base.enable
      System.err.println("3")
      val result = IO.unsafeAttempt(action)
      System.err.println("4")

      result match {
        case Left(e) =>
          e.printStackTrace()
          getLogger.severe(s"Could not enable plugin $getName.")
          Bukkit.getPluginManager.disablePlugin(this)
        case Right(initialState) =>
          state = Some(initialState)
        case x@_ =>
          System.err.println(s"wtf $x")
      }
    }

    override def onDisable(): Unit = {
      val currentState: S = state.getOrElse(throw new IllegalStateException("State is not defined."))
      IO.unsafeRun(base.disable(currentState))
      state = None
    }

    override def onCommand(commandSender: BukkitCommandSender, command: BukkitCommand, alias: String, args: Array[String]): Boolean =
      runState(base.onCommand(IO.mutable(commandSender), IO.mutable(command), alias, args.toList))

    override def onTabComplete(commandSender: BukkitCommandSender, command: BukkitCommand, alias: String, args: Array[String]): java.util.List[String] =
      runState(base.onTabComplete(IO.mutable(commandSender), IO.mutable(command), alias, args.toList)).asJava
  }
}
