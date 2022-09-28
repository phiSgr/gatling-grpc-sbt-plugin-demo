package computerdatabase

import com.github.phisgr.gatling.grpc.Predef._
import io.gatling.core.Predef._
import io.grpc.health.v1.health.HealthCheckResponse.ServingStatus.SERVING
import io.grpc.health.v1.health.{HealthCheckRequest, HealthGrpc}
import com.authzed.api.v1.permission_service.{WriteRelationshipsRequest, WriteRelationshipsResponse, PermissionsServiceGrpc}

class BasicItSimulation extends Simulation {

  val grpcConf = grpc(managedChannelBuilder("localhost", 50051))
  val request = grpc("request_1")
    .rpc(HealthGrpc.METHOD_CHECK)
    .payload(HealthCheckRequest.defaultInstance)
    .extract(_.status.some)(_ is SERVING)

  val writeRelationshipRequest = grpc("write_relationship_request")
    .rpc(PermissionsServiceGrpc.METHOD_WRITE_RELATIONSHIPS)
    .payload(WriteRelationshipsRequest.defaultInstance)
//    .extract(_.writtenAt)(_ is SERVING)

  val scn = scenario("Writing permissions")
    .exec(writeRelationshipRequest)
//    .pause(7.seconds)
//    .exec(request)
//    .exec(request)
//    .exec(request)

  setUp(scn.inject(atOnceUsers(1)).protocols(grpcConf))
}
