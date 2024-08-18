package entities

import play.api.libs.json.{Format, Json, Writes}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

case class User(
                 id: Option[Int] = None,
                 userName: String,
                 password: String,
                 email: String,
                 phone: String,
                 about: Option[String] = None
               )

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def userName: Rep[String] = column[String]("user_name")

  def password: Rep[String] = column[String]("password")

  def email: Rep[String] = column[String]("email")

  def phone: Rep[String] = column[String]("phone")

  def about: Rep[Option[String]] = column[Option[String]]("about")

  override def * : ProvenShape[User] = (id, userName, password, email, phone, about) <> ((User.apply _).tupled, User.unapply)
}

object Users {
  val query = TableQuery[Users]
}


object User {
  implicit val userFormat: Format[User] = Json.format[User]
  implicit val userWrites: Writes[User] = Json.writes[User]

}
