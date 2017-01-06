package sio.bukkit.example

import sio.bukkit.core.Plugin
import sio.core.{SafeApp, IO}
import sio.teletype._

import scala.collection.mutable.ArrayBuffer

class Main extends Plugin.Impl[Unit](new Plugin.Safe[Unit] {
  override def enable: IO[Unit] = {
    System.err.println("1")
    var r: IO[Unit] = null
    try {
      r = IO.trace("test")
    } catch {
      case e: Throwable =>
        System.err.println("1.1")
        val buffer: ArrayBuffer[StackTraceElement] = ArrayBuffer.empty
        var last: StackTraceElement = null
        e.getStackTrace.foreach { s =>
          if (s != last) {
            buffer.append(s)
            last = s
          }
        }
        buffer.foreach(s => System.err.println(s"${s.getClassName} ${s.getMethodName}"))
        System.err.println(e.getClass.getSimpleName)
    }
    System.err.println("2")
    r
  }
})

object Main extends SafeApp {
  def run(args: List[String]): IO[Unit] = putStrLn("Hello.")
}
