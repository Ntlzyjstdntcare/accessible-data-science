assemblyMergeStrategy in assembly <<= (assemblyMergeStrategy in assembly) { (old) => {
  case PathList(ps@_*) if ps.last contains ".class" => MergeStrategy.first
  case x => old(x)
}}

name := "ingestion"

version := "0.1"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  val sparkVersion = "1.5.2"

  Seq(
    "org.scalatest"               %% "scalatest"                    % "2.1.7"     % "test",
    "com.typesafe.akka"           %% "akka-actor"                   % akkaV,
    "com.typesafe.akka"           %% "akka-testkit"                 % akkaV,
    "com.typesafe.scala-logging"  %% "scala-logging"                % "3.1.0",
    "com.datastax.spark"          %% "spark-cassandra-connector"    % "1.2.3",
    "io.spray"                    %% "spray-can"                    % sprayV,
    "io.spray"                    %% "spray-routing"                % sprayV,
    "io.spray"                    %% "spray-testkit"                % sprayV,
    "io.spray"                    %% "spray-client"                 % sprayV,
    "io.spray"                    %% "spray-json"                   % "1.3.2",
    "org.apache.httpcomponents"   % "httpcore"                      % "4.0-beta1",
    "org.apache.spark"            %% "spark-core"                   % sparkVersion % "compile",
    "org.apache.spark"            %% "spark-sql"                    % sparkVersion % "compile",
    "org.apache.spark"            %% "spark-streaming"              % sparkVersion % "compile",
    "org.json4s"                  %% "json4s-native"                % "3.2.11",
    "net.debasishg"               %% "redisclient"                  % "3.0",
    "com.google.code.gson"        % "gson"                          % "2.6.1"
  )
}
