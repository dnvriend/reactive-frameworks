/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.component.google.controller

import javax.inject.Inject

import akka.actor.ActorSystem
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json.Json
import play.api.libs.ws.{ WSClient, WSResponse }
import play.api.mvc.{ Action, AnyContent, Controller, Result }
import akka.pattern.{ CircuitBreaker, after }

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ ExecutionContext, Future }
import scalaz._
import Scalaz._

class GoogleController @Inject() (ws: WSClient, cb: CircuitBreaker)(implicit ec: ExecutionContext, system: ActorSystem) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  def withTimeout(duration: FiniteDuration = 100.millis)(f: Future[WSResponse]): Future[String] =
    Future.firstCompletedOf(List(f.map(_.body), after(duration, system.scheduler)(Future.successful("timeout"))))

  def google: Future[Map[String, String]] = withTimeout()(ws.url("http://www.google.nl").get()).map(body => Map("google" -> body))
  def twitter: Future[Map[String, String]] = withTimeout()(ws.url("http://www.twitter.com").get()).map(body => Map("twitter" -> body))
  def lightbend: Future[Map[String, String]] = withTimeout()(ws.url("http://www.lightbend.com").get()).map(body => Map("lightbend" -> body))

  def compose: Action[AnyContent] = Action.async {
    // because Scala does not support applicative expressions
    val result: Future[Result] = (google |@| twitter |@| lightbend)(_ |+| _ |+| _).map(m => Ok(Json.toJson(m)))
    cb.withCircuitBreaker(result)
  }
}
