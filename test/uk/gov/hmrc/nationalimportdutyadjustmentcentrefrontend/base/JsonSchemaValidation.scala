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

import java.io.InputStream

import com.eclipsesource.schema._
import play.api.libs.json._

import scala.io.Source

trait JsonSchemaValidation {

  private val validator = SchemaValidator()

  def validateJsonAgainstSchema(json: JsValue, jsonSchema: JsValue): Option[JsError] = {
    val schema = Json.fromJson[SchemaType](jsonSchema).getOrElse(
      throw new IllegalStateException("SchemaType cannot be converted from json")
    )

    val result: JsResult[JsValue] = validator.validate(schema, json)
    result match {
      case JsSuccess(_, _) =>
        None
      case JsError(_) =>
        Some(result.asInstanceOf[JsError])
    }
  }

  def loadSchema(path: String): JsValue =
    Json.parse(findResource(path).stripMargin)

  private def findResource(path: String): String = {
    val resource = getClass.getResourceAsStream(path)
    if (resource == null)
      throw new IllegalStateException(s"Could not find resource '$path'")
    else
      readStreamToString(resource)
  }

  private def readStreamToString(is: InputStream): String =
    try Source.fromInputStream(is).mkString
    finally is.close()

}
