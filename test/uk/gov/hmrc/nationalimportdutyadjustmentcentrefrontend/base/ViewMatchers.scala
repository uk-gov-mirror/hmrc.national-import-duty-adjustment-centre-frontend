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
import org.scalatest.matchers.{MatchResult, Matcher}
import play.api.i18n.Messages
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

  def includeMessage(key: String, args: Any*)(implicit messages: Messages): Matcher[Element] =
    new ElementContainsMessageMatcher(key, args)

  def containMessage(key: String, args: Any*)(implicit messages: Messages): Matcher[Elements] =
    new ElementsContainsMessageMatcher(key, args)

  def haveSummaryKey(value: String)   = new ElementsHasElementsContainingTextMatcher("govuk-summary-list__key", value)
  def haveSummaryValue(value: String) = new ElementsHasElementsContainingTextMatcher("govuk-summary-list__value", value)

  def haveSummaryChangeLinkText(value: String) =
    new ElementsHasElementsContainingTextMatcher("govuk-summary-list__actions", value)

  def haveAttribute(key: String, value: String): Matcher[Element] = new ElementHasAttributeValueMatcher(key, value)
  def haveValue(value: String): Matcher[Element]                  = new ElementHasAttributeValueMatcher("value", value)

  def haveNavigatorBackLink(href: String)(implicit messages: Messages): Matcher[Element] =
    new ElementHasNavigatorBacklinkMatcher(href)

  def haveFieldError(fieldName: String, content: String)(implicit messages: Messages): Matcher[Element] =
    new ContainElementWithIDMatcher(s"$fieldName-error") and new ElementContainsGovukFieldError(
      fieldName,
      messages(content)
    )

  def haveSummaryError(key: String)(implicit messages: Messages): Matcher[Element] =
    new ContainErrorSummaryWithMessage(messages(key))

  def beEmpty: Matcher[Elements] = (left: Elements) => {
    MatchResult(left.size() == 0, "Elements was not empty", "Elements was empty")
  }

  def notBePresent: Matcher[Element] = (left: Element) => {
    MatchResult(left == null, "Element was present", "Element was not present")
  }

  class ElementContainsMessageMatcher(key: String, args: Seq[Any])(implicit messages: Messages)
      extends Matcher[Element] {

    override def apply(left: Element): MatchResult = {
      val message = messages(key, args: _*)
      MatchResult(
        left != null && left.text().contains(message),
        s"Element did not contain message {$message}\n${actualContentWas(left)}",
        s"Element contained message {$message}"
      )
    }

  }

  class ElementsContainsMessageMatcher(key: String, args: Seq[Any])(implicit messages: Messages)
      extends Matcher[Elements] {

    override def apply(left: Elements): MatchResult = {
      val message = messages(key, args: _*)
      MatchResult(
        left != null && left.text().contains(message),
        s"Elements did not contain message {$message}\n${actualContentWas(left)}",
        s"Elements contained message {$message}"
      )
    }

  }

  class ElementsHasElementsContainingTextMatcher(elementsClass: String, value: String) extends Matcher[Elements] {

    override def apply(left: Elements): MatchResult =
      MatchResult(
        left != null && left.first().getElementsByClass(elementsClass).text() == value,
        s"Elements with class {$elementsClass} had text {${left.first().getElementsByClass(elementsClass).text()}}, expected {$value}",
        s"Element with class {$elementsClass} had text {${left.first().getElementsByClass(elementsClass).text()}}"
      )

  }

  class ElementHasAttributeValueMatcher(key: String, value: String) extends Matcher[Element] {

    override def apply(left: Element): MatchResult =
      MatchResult(
        left != null && left.attr(key) == value,
        s"Element attribute {$key} had value {${left.attr(key)}}, expected {$value}",
        s"Element attribute {$key} had value {$value}"
      )

  }

  class ElementHasNavigatorBacklinkMatcher(href: String)(implicit messages: Messages) extends Matcher[Element] {

    protected def hasNavigatorBacklink: Element => Boolean = (view: Element) => {
      val link = view.getElementsByClass("govuk-back-link").first()
      link.text() == messages("site.back") &&
      link.attr("href") == href
    }

    override def apply(left: Element): MatchResult =
      MatchResult(
        left != null && hasNavigatorBacklink(left),
        s"Element did not have NavigatorBackLink",
        s"Element had NavigatorBackLink"
      )

  }

  class ContainElementWithIDMatcher(id: String) extends Matcher[Element] {

    override def apply(left: Element): MatchResult =
      MatchResult(
        left != null && left.getElementById(id) != null,
        s"Document did not contain element with ID {$id}\n${actualContentWas(left)}",
        s"Document contained an element with ID {$id}"
      )

  }

  class ElementContainsGovukFieldError(fieldName: String, content: String = "") extends Matcher[Element] {

    override def apply(left: Element): MatchResult = {
      val element           = left.getElementById(s"$fieldName-error")
      val fieldErrorElement = if (element == null) left else element
      MatchResult(
        fieldErrorElement.text().contains(content),
        s"Element did not contain {$content}\n${actualContentWas(fieldErrorElement)}",
        s"Element contained {$content}"
      )
    }

  }

  class ContainErrorSummaryWithMessage(text: String) extends Matcher[Element] {

    override def apply(left: Element): MatchResult =
      MatchResult(
        left != null && left.getElementsByClass("govuk-error-summary__list").text().contains(text),
        s"Document did not contain error element with message {$text}\n${actualContentWas(left)}",
        s"Document contained an error element with message {$text}"
      )

  }

  private def actualContentWas(node: Elements): String =
    if (node == null)
      "Elements did not exist"
    else
      s"\nActual content was:\n${node.html}\n"

  private def actualContentWas(node: Element): String =
    if (node == null)
      "Element did not exist"
    else
      s"\nActual content was:\n${node.html}\n"

}
