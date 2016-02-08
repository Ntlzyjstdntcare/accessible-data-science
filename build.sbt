name := "Accessible Data Science"

version := "0.1"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"

  Seq(
    "org.scalatest"     %% "scalatest"     % "2.1.7" % "test",
    "com.typesafe.akka" %% "akka-actor"    % akkaV,
    "com.typesafe.akka" %% "akka-testkit"  % akkaV,
    "io.spray"          %% "spray-can"     % sprayV,
    "io.spray"          %% "spray-routing" % sprayV,
    "io.spray"          %% "spray-testkit" % sprayV,
    "io.spray"          %% "spray-json"    % "1.3.2"
  )
}
