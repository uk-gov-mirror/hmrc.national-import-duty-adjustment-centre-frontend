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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendClaim, AmendClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  Claim,
  CreateClaimAudit,
  CreateClaimResponse,
  ReclaimDutyType
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis.{EISAmendCaseRequest, EISCreateCaseRequest}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.requests.{
  AmendEISClaimRequest,
  CreateEISClaimRequest
}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClaimService @Inject() (auditConnector: AuditConnector, connector: NIDACConnector) {

  private val APPLICATION_TYPE_NIDAC            = "NIDAC"
  private val ORIGINATING_SYSTEM_DIGITAL        = "Digital"
  private val acknowledgementReferenceMaxLength = 32

  def submitClaim(claim: Claim)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[CreateClaimResponse] = {

    val correlationId = hc.requestId.map(_.value).getOrElse(UUID.randomUUID().toString)
    val eisRequest: EISCreateCaseRequest = EISCreateCaseRequest(
      AcknowledgementReference = correlationId.replace("-", "").takeRight(acknowledgementReferenceMaxLength),
      ApplicationType = APPLICATION_TYPE_NIDAC,
      OriginatingSystem = ORIGINATING_SYSTEM_DIGITAL,
      Content = EISCreateCaseRequest.Content(claim)
    )

    connector.submitClaim(CreateEISClaimRequest(eisRequest, claim.uploads), correlationId)
      .map { response =>
        auditConnector.sendExplicitAudit("CreateClaim", CreateClaimAudit(response.error.isEmpty, claim, response))
        response
      }

  }

  def amendClaim(amendClaim: AmendClaim)(implicit hc: HeaderCarrier): Future[AmendClaimResponse] = {

    val correlationId = hc.requestId.map(_.value).getOrElse(UUID.randomUUID().toString)
    val eisRequest: EISAmendCaseRequest = EISAmendCaseRequest(
      AcknowledgementReference = correlationId.replace("-", "").takeRight(acknowledgementReferenceMaxLength),
      ApplicationType = APPLICATION_TYPE_NIDAC,
      OriginatingSystem = ORIGINATING_SYSTEM_DIGITAL,
      Content = EISAmendCaseRequest.Content(amendClaim)
    )

    connector.amendClaim(AmendEISClaimRequest(eisRequest, amendClaim.uploads), correlationId)
  }

}
