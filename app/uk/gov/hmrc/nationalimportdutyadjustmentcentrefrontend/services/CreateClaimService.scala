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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services

import java.util.UUID

import javax.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis.EISCreateCaseRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{Claim, CreateClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.requests.CreateEISClaimRequest

import scala.concurrent.{ExecutionContext, Future}

class CreateClaimService @Inject() (connector: NIDACConnector)(implicit ec: ExecutionContext) {
  private val acknowledgementReferenceMaxLength = 32

  def submitClaim(claim: Claim)(implicit hc: HeaderCarrier): Future[CreateClaimResponse] = {

    val correlationId = hc.requestId.map(_.value).getOrElse(UUID.randomUUID().toString)
    val eisRequest: EISCreateCaseRequest = EISCreateCaseRequest(
      AcknowledgementReference = correlationId.replace("-", "").takeRight(acknowledgementReferenceMaxLength),
      ApplicationType = "NIDAC",
      OriginatingSystem = "Digital",
      Content = EISCreateCaseRequest.Content(claim)
    )

    connector.submitClaim(CreateEISClaimRequest(eisRequest, claim.uploads), correlationId)

  }

}
