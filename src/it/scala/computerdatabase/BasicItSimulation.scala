package computerdatabase

import com.authzed.api.v1.core.RelationshipUpdate.Operation
import com.authzed.api.v1.core.{ObjectReference, Relationship, RelationshipUpdate, SubjectReference}
import com.authzed.api.v1.permission_service.{PermissionsServiceGrpc, WriteRelationshipsRequest}
import com.github.phisgr.gatling.grpc.Predef._
import io.gatling.core.Predef._
import io.grpc.{Metadata, Status}
import scalapb.UnknownFieldSet

class BasicItSimulation extends Simulation {

  var sharedKey = "Bearer somerandomkeyhere"
  val TokenHeaderKey: Metadata.Key[String] = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
  val grpcConf = grpc(managedChannelBuilder("localhost", 50051)
    .disableRetry()
    .usePlaintext()
  )

  val relationship = Relationship.of(
    Option(ObjectReference("account", "456")),
    "holder_account_party",
    Option(SubjectReference(Option(ObjectReference("login", "123")), "", UnknownFieldSet.empty)))
  val relationshipUpdates = Seq(new RelationshipUpdate(Operation.OPERATION_CREATE, Option(relationship)))

  val writeRelationshipRequest = grpc("write_relationship_request")
    .rpc(PermissionsServiceGrpc.METHOD_WRITE_RELATIONSHIPS)
    .payload(WriteRelationshipsRequest.of(relationshipUpdates, Seq.empty))
    .header(TokenHeaderKey)(sharedKey)
    .check(statusCode is Status.Code.OK)

  val scn = scenario("Writing permissions")
    .exec(writeRelationshipRequest)

  setUp(scn.inject(atOnceUsers(1)).protocols(grpcConf))
}
