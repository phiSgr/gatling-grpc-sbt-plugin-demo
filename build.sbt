enablePlugins(GatlingPlugin)

scalaVersion := "2.13.4"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

val gatlingVersion = "3.5.0"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % gatlingVersion % "test,it"
libraryDependencies += "com.github.phisgr" %% "gatling-grpc" % "0.11.0" % "test,it"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
)

Project.inConfig(IntegrationTest)(baseAssemblySettings)
// You fat JAR will be in target/scala-2.13/grpc-test-bundle.jar
assemblyJarName in IntegrationTest := s"grpc-test-bundle.jar"
// Exclude libraries that are already present in the Gatling bundle
assemblyExcludedJars in assembly in IntegrationTest := {
  val gatlingHome = file(sys.props("user.home")) / "Downloads" / "gatling-charts-highcharts-bundle-3.5.0"
  val libFiles = Option((gatlingHome / "lib").list).getOrElse {
    throw new IllegalStateException(s"Gatling lib not found in $gatlingHome")
  }

  val gatlingLibs = Set("gatling-test-framework-3.5.0.jar") ++ libFiles

  (fullClasspath in IntegrationTest).value.filter { classPath =>
    gatlingLibs.contains(classPath.data.getName)
  }
}
assemblyMergeStrategy in assembly in IntegrationTest := {
  case PathList("META-INF", "versions", _, "module-info.class") | "module-info.class" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
