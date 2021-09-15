package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import java.time.LocalDateTime

class TestResponseSaved extends Simulation{

  //http configuration
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

  val responseBody_writer = {
    val fos = new java.io.FileOutputStream("ResponseBody.txt")
    new java.io.PrintWriter(fos,true)
  }

  //Scenario
  val scn = scenario("Initiate call")
    .exec(http("post initiate call")
      .post(s"/api/merchants/${retailerKey}/transactions/installments/initiate")
      .body(RawFileBody("./src/test/resources/RequestBodies/AuroraInitiate.json")).asJson
      .headers(initialHeaders)
      .header("x-timestamp" , s"$timeStamp")
      .basicAuth("1EjBIkwnJEhJVxWxwYyCDm1hZ8Hwr11a9m39DwauNj2nKK7qJd", "jwNUapPTCBhXMGubVIDJ4M7r8R6nv5tKhKw8Ens4dzNhyeL2kq")
//      .disableFollowRedirect
//      .disableUrlEncoding
//      .check(bodyString.saveAs("partnerURL"))
      .check(status is 200)
//      .check(bodyString.saveAs("BODY"))
      .check(jsonPath("$.partnerURL").saveAs("partnerURL"))
    )

    .exec(session => {
//      val response = session("BODY").as[String]
      responseBody_writer.println(session("partnerURL").as[String])
      val new_url = session("partnerURL").as[String]
//      println(s"Response body: \n$response")
      println(s"URL to be requested: \n$new_url")

//      http("Create Contract Home")
//        .get("")
//        .header("Accept", "*/*")
//        .header("Accept-Encoding", "gzip, deflate, br")
//        .header("Connection", "Keep-Alive")
//        .check(status is 200)
      session
    })
//      .check(regex("""https://(.*)/.*""").saveAs("partnerURL")))

//

//    .pause(3)
//
//    .exec(http("Create Contract")
//      .get(s"${partnerURL}")
//      .check(status is 200)
//    )

  //setup
  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpConf)

}