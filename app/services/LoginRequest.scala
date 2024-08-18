package services
import play.api.data.Form
import play.api.data.Forms._

case class LoginRequest(username: String, password: String)

object LoginRequest {
  val form: Form[LoginRequest] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginRequest.apply)(LoginRequest.unapply)
  )
}
