package utils

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.Json
import java.time.Clock

object JwtUtil {
  private val secretKey = "9DaT4XmRiRUuhARqD2oM0GNcWavwuh5tMYEqgKpL9NI="
  private val algorithm = JwtAlgorithm.HS256
  private val clock = Clock.systemUTC() // Create a Clock instance

  def createToken(username: String, expirationTime: Long = 3600): String = {
    val claim = JwtClaim(
      content = Json.obj("username" -> username).toString,
      expiration = Some(clock.instant.plusSeconds(expirationTime).getEpochSecond),
      issuedAt = Some(clock.instant.getEpochSecond)
    )
    Jwt.encode(claim, secretKey, algorithm)
  }

  def validateToken(token: String): Option[JwtClaim] = {
    Jwt.decode(token, secretKey, Seq(algorithm)).toOption
  }

  def extractUsername(claim: JwtClaim): Option[String] = {
    (Json.parse(claim.content) \ "username").asOpt[String]
  }
}
