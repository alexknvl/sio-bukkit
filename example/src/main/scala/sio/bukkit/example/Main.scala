package sio.bukkit.example

import sio.bukkit.core.{Server, Plugin}
import sio.core.IO
import sio.teletype._

class Main extends Plugin.Impl[Unit](new Plugin.Safe[Unit] {
  override def enable: IO[Unit] = for {
    version <- Server.getServerVersion
    address <- Server.getServerAddress
    _       <- putStrLn(s"Hello! Running $version on $address")
  } yield ()
})
