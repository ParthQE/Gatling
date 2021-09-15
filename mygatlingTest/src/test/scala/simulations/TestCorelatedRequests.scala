package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import java.time.LocalDateTime
import scala.concurrent.duration.DurationInt

class TestCorelatedRequests extends Simulation {

  val merchantApiUrl = "https://playground.checkout.paybright.com"
  val retailerKey = 54680
  val timeStamp = LocalDateTime.now()



  val httpConf = http.proxy(Proxy("localhost", 8866))
    .baseUrl(s"$merchantApiUrl")

  val initialHeaders = Map(
    "Accept" -> "*/*",
    "Content-Type" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive",
    "Accept-Language" -> "En",
    "x-country-code" -> "CA",
    "x-request-id" -> "ABC",
    "x-conversion-id" -> "abc",
    "connection" -> "keep-alive",
    "x-payer-id" -> "jghjgvh"
  )

  //Scenario
  val scn = scenario("Initiate call")
    .exec(http("post initiate call")
      .post(s"/api/merchants/${retailerKey}/transactions/installments/initiate")
      .body(RawFileBody("./src/test/resources/RequestBodies/AuroraInitiate.json")).asJson
      .headers(initialHeaders)
      .header("x-timestamp" , s"$timeStamp")
      .basicAuth("1EjBIkwnJEhJVxWxwYyCDm1hZ8Hwr11a9m39DwauNj2nKK7qJd", "jwNUapPTCBhXMGubVIDJ4M7r8R6nv5tKhKw8Ens4dzNhyeL2kq")
      .check(status is 200)
      .check(bodyString.saveAs("BODY"))
      .check(jsonPath("$.partnerURL").saveAs("partnerURL"))
    )
    .pause(2)
    .exec(
      http("test create")
        .get("${partnerURL}")
        .check(status is 200)
    )
    .pause(2)

  setUp(
    scn.inject(
      nothingFor(4.seconds), // 1
      atOnceUsers(1000), // 2
      rampUsers(5).during(5.seconds), // 3
//      constantUsersPerSec(20).during(15.seconds), // 4
//      constantUsersPerSec(20).during(15.seconds).randomized, // 5
//      rampUsersPerSec(10).to(20).during(10.minutes), // 6
//      rampUsersPerSec(10).to(20).during(10.minutes).randomized, // 7
//      heavisideUsers(1000).during(20.seconds) // 8)
  ).protocols(httpConf))
}
