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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create

import play.api.libs.json._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.RepresentationType.Representative
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models._

final case class CreateAnswers(
  contactDetails: Option[ContactDetails] = None,
  claimantAddress: Option[Address] = None,
  representationType: Option[RepresentationType] = None,
  claimType: Option[ClaimType] = None,
  claimReason: Option[ClaimReason] = None,
  reclaimDutyTypes: Set[ReclaimDutyType] = Set.empty,
  reclaimDutyPayments: Map[String, DutyPaid] = Map.empty,
  bankDetails: Option[BankDetails] = None,
  importerContactDetails: Option[ImporterContactDetails] = None,
  repayTo: Option[RepayTo] = None,
  entryDetails: Option[EntryDetails] = None,
  itemNumbers: Option[ItemNumbers] = None,
  uploads: Seq[UploadedFile] = Seq.empty,
  uploadAnotherFile: Option[Boolean] = None,
  importerHasEori: Option[Boolean] = None,
  importerEori: Option[EoriNumber] = None
) {

  val isRepresentative: Boolean     = representationType.contains(Representative)
  val doesImporterHaveEori: Boolean = importerHasEori.contains(true)
}

object CreateAnswers {

  implicit val formats: OFormat[CreateAnswers] = Json.format[CreateAnswers]
}
