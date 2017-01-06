package sio.bukkit

import cats.instances.function._
import cats.syntax.all._

import sio.bukkit.core.ItemStack
import sio.core._

import scala.reflect.ClassTag

/**
  * Created by alex on 1/6/17.
  */
package object nbt {
  type MinecraftItemStack    = net.minecraft.server.ItemStack
  type MinecraftNBTBase      = net.minecraft.server.NBTBase
  type MinecraftNBTEnd       = net.minecraft.server.NBTTagEnd
  type MinecraftNBTByte      = net.minecraft.server.NBTTagByte
  type MinecraftNBTShort     = net.minecraft.server.NBTTagShort
  type MinecraftNBTInt       = net.minecraft.server.NBTTagInt
  type MinecraftNBTLong      = net.minecraft.server.NBTTagLong
  type MinecraftNBTFloat     = net.minecraft.server.NBTTagFloat
  type MinecraftNBTDouble    = net.minecraft.server.NBTTagDouble
  type MinecraftNBTByteArray = net.minecraft.server.NBTTagByteArray
  type MinecraftNBTString    = net.minecraft.server.NBTTagString
  type MinecraftNBTList      = net.minecraft.server.NBTTagList
  type MinecraftNBTCompound  = net.minecraft.server.NBTTagCompound
  type MinecraftNBTIntArray  = net.minecraft.server.NBTTagIntArray

  private[bukkit] def bukkit2nbt(value: MinecraftNBTBase): NBTIO = value match {
    case x: MinecraftNBTEnd => NBTIO.EndTag(x)
    case x: MinecraftNBTByte => NBTIO.ByteTag(x)
    case x: MinecraftNBTShort => NBTIO.ShortTag(x)
    case x: MinecraftNBTInt => NBTIO.IntTag(x)
    case x: MinecraftNBTLong => NBTIO.LongTag(x)
    case x: MinecraftNBTFloat => NBTIO.FloatTag(x)
    case x: MinecraftNBTDouble => NBTIO.DoubleTag(x)
    case x: MinecraftNBTByteArray => NBTIO.ByteArrayTag(x)
    case x: MinecraftNBTString => NBTIO.StringTag(x)
    case x: MinecraftNBTList => NBTIO.ListTag(x)
    case x: MinecraftNBTCompound => NBTIO.CompoundTag(x)
    case x: MinecraftNBTIntArray => NBTIO.IntArrayTag(x)
  }

  sealed trait NBTIO extends Any with Serializable with Product {
    private[bukkit] def toBukkit: MinecraftNBTBase
  }
  object NBTIO {
    final case class EndTag(value: MinecraftNBTEnd) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class ByteTag(value: MinecraftNBTByte) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class ShortTag(value: MinecraftNBTShort) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class IntTag(value: MinecraftNBTInt) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class LongTag(value: MinecraftNBTLong) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class FloatTag(value: MinecraftNBTFloat) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class DoubleTag(value: MinecraftNBTDouble) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class ByteArrayTag(value: MinecraftNBTByteArray) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
      def get(i: Int): IO[Byte] = IO { value.c()(i) }
      def set(i: Int, v: Byte): IO[Unit] = IO { value.c()(i) = v }
    }
    final case class StringTag(value: MinecraftNBTString) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
    }
    final case class ListTag(value: MinecraftNBTList) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
      def get(i: Int): IO[NBTIO] = IO {
        require(0 <= i && i < value.size())
        bukkit2nbt(value.h(i))
      }
      def set(i: Int, v: NBTIO): IO[Unit] = IO {
        require(0 <= i && i < value.size())
        value.a(i, v.toBukkit)
      }
    }
    final case class CompoundTag(value: MinecraftNBTCompound) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value

      def get(s: String): IO[Option[NBTIO]] = IO { Option.apply(value.get(s)).map(bukkit2nbt) }
      def set(s: String, v: NBTIO): IO[Unit] = IO { value.set(s, v.toBukkit) }
    }
    final case class IntArrayTag(value: MinecraftNBTIntArray) extends AnyVal with NBTIO {
      override private[bukkit] def toBukkit: MinecraftNBTBase = value
      def get(i: Int): IO[Int] = IO { value.d()(i) }
      def set(i: Int, v: Int): IO[Unit] = IO { value.d()(i) = v }
    }
  }

  sealed trait Field[S, A] { self =>
    def get(obj: S): IO[A]
    def modify(obj: S, f: A => A): IO[A]
    def set(obj: S, value: A): IO[Unit]

    def omap[T](f: T => S): Field[T, A] = new Field[T, A] {
      override def get(obj: T): IO[A] = self.get(f(obj))
      override def modify(obj: T, func: (A) => A): IO[A] = self.modify(f(obj), func)
      override def set(obj: T, value: A): IO[Unit] = self.set(f(obj), value)
    }

    def fmap[B](f: A => B, g: B => A): Field[S, B] = new Field[S, B] {
      override def get(obj: S): IO[B] = self.get(obj).map(f)
      override def modify(obj: S, func: (B) => B): IO[B] = self.modify(obj, a => g(func(f(a)))).map(f)
      override def set(obj: S, value: B): IO[Unit] = self.set(obj, g(value))
    }
  }
  object Field {
    def reflectDeclared[S, A](name: String)(implicit S: ClassTag[S]): Field[S, A] = new Field[S, A] {
      private[this] lazy val field = {
        val result = S.runtimeClass.getDeclaredField(name)
        result.setAccessible(true)
        result
      }

      override def get(s: S): IO[A] = IO { field.get(s).asInstanceOf[A] }
      override def modify(s: S, f: (A) => A): IO[A] = IO {
        val value = field.get(s).asInstanceOf[A]
        val newValue = f(value)
        field.set(s, newValue)
        newValue
      }
      override def set(s: S, a: A): IO[Unit] = IO { field.set(s, a) }
    }
  }

  private[bukkit] val itemStackHandleField =
    Field.reflectDeclared[org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack, MinecraftItemStack]("handle")
        .omap((i: ItemStack) => i.unsafeValue.asInstanceOf[org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack])

  implicit class ItemStackNBTOps(handle: ItemStack) extends Mutable.Syntax(handle) {
    def setTag(tag: NBTIO.CompoundTag): IO[Unit] =
      itemStackHandleField.get(handle).liftMap(_.setTag(tag.value))

    def getTag: IO[NBTIO.CompoundTag] =
      itemStackHandleField.get(handle).liftMap(is => NBTIO.CompoundTag(is.getTag()))
  }
}
