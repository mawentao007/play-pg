package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, name: String, password: String)

object User {
//  object Country extends Magic[Country](Some("Countries"))
  // -- Parsers

  // access "orders" database instead of "default"
    /*  DB.withConnection("orders") { conn =>
         do whatever you need with the connection
      }*/
  //默认参数是default,可以自己加数据源
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("users.email") ~
    get[String]("users.name") ~
    get[String]("users.password") map {
      case email~name~password => User(email, name, password)
    }
  }
  
  // -- Queries
  //withConnection 会自动关闭连接
  //withConnection默认是去找default配置的连接。
  /**
   * Retrieve a User from email.
   * 括号中可以视为一个函数,参数是connection,如果不加implicit,函数其实是正确的,但是因为as部分需要一个
   * implicit的connection作为参数,因此需要声明connection为implicit给它用
   * 比较晦涩,不过还是很有意思.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email = {email}").on(
        'email -> email
      ).executeQuery().as(User.simple.singleOpt)

    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)  //as这个函数需要connection作为隐含参数,因此需要在connection前面加implicit
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from users where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into users values (
            {email}, {name}, {password}
          )
        """
      ).on(
        'email -> user.email,
        'name -> user.name,
        'password -> user.password
      ).executeUpdate()
      
      user
      
    }
  }
  
}
