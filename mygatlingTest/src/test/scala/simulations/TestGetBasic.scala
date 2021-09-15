package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TestGetBasic extends Simulation{

  // Set HTTP Configuration
  val httpConf = http.baseUrl("https://reqres.in/")
    .header("connection", "Keep-Alive")

  val initialHeaders = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br"
  )

  //Set up scenario
  val scn = scenario("Get Users")
    .exec(
      http("Get Users list")
        .get("/api/users?page=2")
        .check(status is 200)
    )

  //Set up users
  setUp(scn.inject(atOnceUsers(users = 1))).protocols(httpConf)
}
