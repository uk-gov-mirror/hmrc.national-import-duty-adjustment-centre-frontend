/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.scalatest.matchers.must.Matchers
import play.api.mvc.Result
import play.api.test.Helpers.{contentAsString, _}
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait ViewMatchers extends Matchers {

  implicit private def elements2Scala(elements: Elements): Iterator[Element] = elements.iterator().asScala
  implicit protected def htmlBodyOf(html: Html): Document                    = Jsoup.parse(html.toString())
  implicit protected def htmlBodyOf(page: String): Document                  = Jsoup.parse(page)
  implicit protected def htmlBodyOf(result: Future[Result]): Document        = htmlBodyOf(contentAsString(result))

}
