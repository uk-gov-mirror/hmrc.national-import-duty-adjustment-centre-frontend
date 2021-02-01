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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings

import java.time.LocalDate

import org.scalatest.OptionValues
import play.api.data.{Form, FormError}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Enumerable

object MappingsSpec {

  sealed trait Foo
  case object Bar extends Foo
  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  }

}

class MappingsSpec extends UnitSpec with OptionValues with Mappings {

  import MappingsSpec._

  "text" must {

    val testForm: Form[String] =
      Form("value" -> text())

    "bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "return a custom error message" in {
      val form   = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "boolean" must {

    val testForm: Form[Boolean] =
      Form("value" -> boolean())

    "bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" must {

    val testForm: Form[Int] =
      Form("value" -> int())

    "bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" must {

    val testForm = Form("value" -> enumerable[Foo]())

    "bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "localDate" must {

    val date      = LocalDate.of(2020, 10, 28)
    val validData = Map("value" -> "28", "value.month" -> "10", "value.year" -> "2020")

    val testForm: Form[LocalDate] =
      Form("value" -> localDate("date.invalid", "date.required"))

    "bind a valid value" in {
      val result = testForm.bind(validData)
      result.get mustEqual date
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "date.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "date.required"))
    }

    "not bind an invalid day format" in {
      val result = testForm.bind(validData + ("value" -> "xx"))
      result.errors must contain(FormError("value", "error.number.nonNumeric"))
    }

    "not bind an invalid day number" in {
      val result = testForm.bind(validData + ("value" -> "32"))
      result.errors must contain(FormError("value", "date.error.day"))
    }

    "not bind an invalid month format" in {
      val result = testForm.bind(validData + ("value.month" -> "10.0"))
      result.errors must contain(FormError("value.month", "error.number.wholeNumber"))
    }

    "not bind an invalid month number" in {
      val result = testForm.bind(validData + ("value.month" -> "13"))
      result.errors must contain(FormError("value.month", "date.error.month"))
    }

    "not bind an invalid year format" in {
      val result = testForm.bind(validData + ("value.year" -> "year"))
      result.errors must contain(FormError("value.year", "error.number.nonNumeric"))
    }

    "not bind missing day" in {
      val result = testForm.bind(validData - "value")
      result.errors must contain(FormError("value", "date.required.day"))
    }

    "not bind missing month" in {
      val result = testForm.bind(validData - "value.month")
      result.errors must contain(FormError("value.month", "date.required.month"))
    }

    "not bind missing year" in {
      val result = testForm.bind(validData - "value.year")
      result.errors must contain(FormError("value.year", "date.required.year"))
    }

    "not bind missing day and month" in {
      val result = testForm.bind(validData - "value" - "value.month")
      result.errors must contain(FormError("value", "date.required.day.month"))
    }

    "not bind missing day and year" in {
      val result = testForm.bind(validData - "value" - "value.year")
      result.errors must contain(FormError("value", "date.required.day.year"))
    }

    "not bind missing month and year" in {
      val result = testForm.bind(validData - "value.month" - "value.year")
      result.errors must contain(FormError("value.month", "date.required.month.year"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(date)
      result.apply("value").value.value mustEqual "28"
      result.apply("value.month").value.value mustEqual "10"
      result.apply("value.year").value.value mustEqual "2020"
    }
  }
}
