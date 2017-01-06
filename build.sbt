val testLibraries = List(
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.typelevel" %% "discipline" % "0.7.3" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test")

val refinedLibrary = List(
  "eu.timepit" %% "refined" % "0.6.1")

val catsLibraries = List(
  "org.typelevel" %% "algebra" % "0.6.0",
  "org.typelevel" %% "cats" % "0.8.1")

val simulacrumLibrary = List(
  "com.github.mpilquist" %% "simulacrum" % "0.10.0")

val shapelessLibrary = List(
  "com.chuusai" %% "shapeless" % "2.3.2")

val iterateeLibraries = ((version: String) => List(
  "io.iteratee"  %%  "iteratee-core" % version,
  "io.iteratee"  %%  "iteratee-files" % version))
  .apply("0.8.0")

val mcVersion = "1.10.2"
val bukkitDependencies = List(
  "org.bukkit" % "bukkit"             % (mcVersion + "-R0.1-SNAPSHOT") % "provided")
val craftbukkitDependencies = bukkitDependencies ++ List(
  "org.bukkit" % "craftbukkit"        % (mcVersion + "-R0.1-SNAPSHOT") % "provided")
val spigotDependencies = craftbukkitDependencies ++ List(
  "org.spigotmc" % "minecraft-server" % (mcVersion + "-SNAPSHOT") % "provided",
  "org.spigotmc" % "spigot-api"       % (mcVersion + "-R0.1-SNAPSHOT") % "provided",
  "org.spigotmc" % "spigot"           % (mcVersion + "-R0.1-SNAPSHOT") % "provided")

lazy val commonSettings = List(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  organization := "com.alexknvl",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.1",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  scalacOptions ++= List(
    "-deprecation", "-unchecked", "-feature",
    "-encoding", "UTF-8",
    "-language:existentials",
    "-language:higherKinds", 
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Yno-adapted-args", "-Ywarn-dead-code",
    "-Ywarn-numeric-widen", "-Xfuture"),
  resolvers ++= List(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases")),
  libraryDependencies ++= testLibraries,
  libraryDependencies ++= ((version: String) => List(
    "com.alexknvl"  %% "sio-core" % version,
    "com.alexknvl"  %% "sio-teletype" % version))
    .apply("0.2.2-SNAPSHOT"),
  libraryDependencies ++= refinedLibrary)


lazy val core = (project in file("core")).
  settings(name := "sio-bukkit-core").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= spigotDependencies)

lazy val nbt = (project in file("nbt")).
  settings(name := "sio-bukkit-nbt").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= spigotDependencies).
  dependsOn(core)

lazy val example = (project in file("example")).
  settings(name := "sio-bukkit-example").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= spigotDependencies).
  settings(proguardSettings: _*).
  settings(
    ProguardKeys.options in Proguard ++= List(
      "-dontnote",
      "-dontwarn",
      "-ignorewarnings",
      "-dontobfuscate",
      "-keep public class sio.bukkit.example.MainImpl { public *; }",
      "-keepattributes Exceptions,Signature,Deprecated,SourceFile,SourceDir,LineNumberTable,Synthetic,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault,InnerClasses,*Annotation*"),
    javaOptions in (Proguard, ProguardKeys.proguard) := Seq("-Xmx2G"),
    ProguardKeys.proguardVersion in Proguard := "5.2.1",
    ProguardKeys.inputFilter in Proguard := { file =>
      file.name match {
        case "scala-library.jar" => Some("!META-INF/**")
        case _                   => Some("!META-INF/**,!**.sjsir,,!**.properties,,!**.xml")
      }
    }).
  dependsOn(core, nbt)

lazy val root = (project in file(".")).
  settings(name := "sio-bukkit").
  settings(commonSettings: _*).
  aggregate(core, nbt, example)
