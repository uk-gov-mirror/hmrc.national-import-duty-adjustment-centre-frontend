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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis

import java.util.UUID

import play.api.libs.json.Json
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{JsonSchemaValidation, TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create._

class EISCreateCaseRequestValidationSpec extends UnitSpec with JsonSchemaValidation with TestData {

  private val jsonSchema = loadSchema("/pega-create-case-spec/RequestJSONSchemaV0.4.JSON")

  "EISCreateCaseRequest" should {

    "validate all claim types" in {
      ClaimType.values.foreach { ct =>
        validationErrors(eisRequest(completeAnswers.copy(claimType = Some(ct)))) mustBe None
      }
    }

    "validate all representation types" in {
      RepresentationType.values.foreach { rt =>
        validationErrors(eisRequest(completeAnswers.copy(representationType = Some(rt)))) mustBe None
      }
    }

    "validate all repay to types" in {
      RepayTo.values.foreach { rt =>
        validationErrors(eisRequest(completeAnswers.copy(repayTo = Some(rt)))) mustBe None
      }
    }

    "validate full post code with space" in {
      validationErrors(
        eisRequest(completeAnswers.copy(claimantAddress = Some(addressAnswer.copy(postCode = "BA12 3HS"))))
      ) mustBe None
    }

    "validate full telephone number" in {
      validationErrors(
        eisRequest(
          completeAnswers.copy(contactDetails =
            Some(contactDetailsAnswer.copy(telephoneNumber = Some("+44 1234 23534343 (ext 123)")))
          )
        )
      ) mustBe None
    }

    "validate contact details without telephone number" in {
      validationErrors(
        eisRequest(
          completeAnswers.copy(contactDetails =
            Some(contactDetailsAnswer.copy(telephoneNumber = None))
          )
        )
      ) mustBe None
    }

  }

  private def eisRequest(answers: CreateAnswers) =
    EISCreateCaseRequest(
      AcknowledgementReference = UUID.randomUUID().toString.replace("-", "").takeRight(32),
      ApplicationType = "NIDAC",
      OriginatingSystem = "Digital",
      Content = EISCreateCaseRequest.Content(Claim(claimantEori, answers))
    )

  private def validationErrors(eisRequest: EISCreateCaseRequest) =
    validateJsonAgainstSchema(Json.toJson(eisRequest), jsonSchema)

}
