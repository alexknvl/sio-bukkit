package sio.bukkit.example

import sio.bukkit.core.{Server, Plugin}
import sio.core.IO
import sio.teletype._

object Main extends Plugin.Safe[Unit] {
  override def enable: IO[Unit] = for {
    version <- Server.getServerVersion
    address <- Server.getServerAddress
    _       <- putStrLn(s"Hello! Running $version on $address")
  } yield ()
}

class MainImpl extends Plugin.Impl[Unit](Main)
