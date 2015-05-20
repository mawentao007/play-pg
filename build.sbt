name := "modern-web-template"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
  )

libraryDependencies ++= Seq(
  jdbc,
  anorm
)

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "_*.less"
//添加文件过滤功能，来筛选sbt要处理的文件．