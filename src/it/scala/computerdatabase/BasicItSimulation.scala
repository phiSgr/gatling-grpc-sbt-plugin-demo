package computerdatabase

import com.github.phisgr.gatling.grpc.Predef._
import io.gatling.core.Predef._
import io.grpc.health.v1.health.{HealthCheckRequest, HealthGrpc}

import scala.concurrent.duration._

class BasicItSimulation extends Simulation {

  val grpcConf = grpc(managedChannelBuilder("photoslibrary.googleapis.com", 443))
  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(
      grpc("request_1")
        .rpc(HealthGrpc.METHOD_CHECK)
        .payload(HealthCheckRequest.defaultInstance)
    )
    .pause(7.seconds)

  setUp(scn.inject(atOnceUsers(1)).protocols(grpcConf))
}
