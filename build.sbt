name := "modern-web-template"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "angularjs" % "1.3.8",
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.mockito" % "mockito-core" % "1.10.17" % "test")

libraryDependencies ++= Seq(
  jdbc,
  anorm
)

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "_*.less"
//添加文件过滤功能，来筛选sbt要处理的文件．