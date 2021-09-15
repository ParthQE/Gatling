package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TestSampleGet extends Simulation{

  //HTTP Configure
  val httpConf = http
    .baseUrl("https://reqres.in/")

  //Building Scenario
  val scn = scenario("Test basic Get")
    .exec(http("Get Users")
      .get("/api/users?page=2")
      .header("Accept", "*/*")
      .check(status is 200)
    )

  //Building the Setup
  setUp(scn.inject(atOnceUsers(users = 1))).protocols(httpConf)

}
