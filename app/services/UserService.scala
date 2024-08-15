package services

import entities.{User, Users}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def createUser(user: User): Future[Option[Int]] = {
    db.run((Users.query returning Users.query.map(_.id) += user).map(id => id))
  }

  def createMultipleUsers(users: Seq[User]): Future[Seq[Int]] = {
    val insertAction = Users.query.returning(Users.query.map(_.id)).++=(users)
    db.run(insertAction).map(_.flatten)
  }

  def updateUser(user: User, id: Int): Future[Int] = {
    val userToUpdate = user.copy(id = Some(id))
    db.run(Users.query.filter(_.id === id).update(userToUpdate))
  }

  def getUser(id: Int): Future[Option[User]] = {
    db.run(Users.query.filter(_.id === id).result.headOption)
  }

  def deleteUser(id: Int): Future[Int] = {
    db.run(Users.query.filter(_.id === id).delete)
  }

  def getAllUsers: Future[Seq[User]] = {
    db.run(Users.query.result)
  }
}
