package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller {

  // -- Authentication

  val loginForm = Form(
    tuple(                           //tuple 创建一个Mapping，verifying在这个Mapping的基础上加上点对点的限制条件和错误消息
      "email" -> text,
      "password" -> text
    )
      verifying ("Invalid email or password", result => result match {         //两路数据绑定的构造函数
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(    //将数据与表格进行绑定，通过验证则不会返回错误消息
      formWithErrors => BadRequest(html.login(formWithErrors)),   //表格验证失败
      user => Redirect(routes.Projects.index).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  // -- Javascript routing

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Projects.add, Projects.delete, Projects.rename,
        Projects.addGroup, Projects.deleteGroup, Projects.renameGroup,
        Projects.addUser, Projects.removeUser, Tasks.addFolder, 
        Tasks.renameFolder, Tasks.deleteFolder, Tasks.index,
        Tasks.add, Tasks.update, Tasks.delete
      )
    ).as("text/javascript") 
  }

}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   * session保存在服务器端，cookie之中有session id，可以访问服务器session。
   * 这里查看请求的session id，如果确实存在，则返回，否则重定向到登陆页面。
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   * 这是一个按参数传递的例子，没有先把结果算出来而是先传进去再算，因为这是安全模块。
   * Security.Authenticated :Wraps another action, allowing only authenticated HTTP requests.包裹另一个请求，
   * 只有通过验证的才能继续
   * f 是一个curried function，两个参数一个结果
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }

  /**
   * Check if the connected user is a owner of this task.
   */
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Task.isOwner(task, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }

}

