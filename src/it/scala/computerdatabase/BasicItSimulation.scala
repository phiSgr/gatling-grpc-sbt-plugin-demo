package computerdatabase

import com.github.phisgr.gatling.grpc.Predef._
import io.gatling.core.Predef._
import io.grpc.health.v1.health.HealthCheckResponse.ServingStatus.SERVING
import io.grpc.health.v1.health.{HealthCheckRequest, HealthGrpc}

import scala.concurrent.duration._

class BasicItSimulation extends Simulation {

  val grpcConf = grpc(managedChannelBuilder("photoslibrary.googleapis.com", 443))
  val request = grpc("request_1")
    .rpc(HealthGrpc.METHOD_CHECK)
    .payload(HealthCheckRequest.defaultInstance)
    .extract(_.status.some)(_ is SERVING)

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(request)
    .pause(7.seconds)
    .exec(request)
    .exec(request)
    .exec(request)

  setUp(scn.inject(atOnceUsers(1)).protocols(grpcConf))
}
