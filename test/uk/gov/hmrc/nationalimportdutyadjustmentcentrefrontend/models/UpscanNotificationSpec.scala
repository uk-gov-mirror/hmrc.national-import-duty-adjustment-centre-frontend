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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import java.time.ZonedDateTime

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UpscanNotificationSpec.{
  failedNotificationBody,
  successNotificationBody
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.{
  FailureDetails,
  Quarantine,
  UploadDetails
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{
  UpscanFileFailed,
  UpscanFileReady,
  UpscanNotification
}

class UpscanNotificationSpec extends UnitSpec {

  "UpscanNotification JSON reader" should {
    "deserialize successful body" in {

      UpscanNotification.reads.reads(Json.parse(successNotificationBody)) mustBe
        JsSuccess(
          UpscanFileReady(
            reference = "11370e18-6e24-453e-b45a-76d3e32ea33d",
            downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
            uploadDetails = UploadDetails(
              uploadTimestamp = ZonedDateTime.parse("2018-04-24T09:30:00Z"),
              checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              fileMimeType = "application/pdf",
              fileName = "test.pdf"
            )
          )
        )
    }

    "deserialize failed body" in {

      UpscanNotification.reads.reads(Json.parse(failedNotificationBody)) mustBe
        JsSuccess(
          UpscanFileFailed(
            reference = "11370e18-6e24-453e-b45a-76d3e32ea33d",
            failureDetails = FailureDetails(failureReason = Quarantine, message = "e.g. This file has a virus")
          )
        )
    }
  }

}

object UpscanNotificationSpec {

  val successNotificationBody =
    """
      |{
      |    "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
      |    "fileStatus" : "READY",
      |    "downloadUrl" : "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      |    "uploadDetails": {
      |        "uploadTimestamp": "2018-04-24T09:30:00Z",
      |        "checksum": "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
      |        "fileName": "test.pdf",
      |        "fileMimeType": "application/pdf"
      |    }
      |}
      |
        """.stripMargin

  val failedNotificationBody =
    """
      |{
      |    "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
      |    "fileStatus" : "FAILED",
      |    "failureDetails": {
      |        "failureReason": "QUARANTINE",
      |        "message": "e.g. This file has a virus"
      |    }
      |}
        """.stripMargin

}
