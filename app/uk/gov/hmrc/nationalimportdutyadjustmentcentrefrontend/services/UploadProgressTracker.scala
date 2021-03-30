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

import com.google.inject.ImplementedBy
import javax.inject.Inject
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{InProgress, UploadStatus}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, UploadId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.{UploadDetails, UploadRepository}

import scala.concurrent.{ExecutionContext, Future}

class MongoBackedUploadProgressTracker @Inject() (repository: UploadRepository)(implicit ec: ExecutionContext)
    extends UploadProgressTracker {

  override def requestUpload(uploadId: UploadId, journeyId: JourneyId, fileReference: Reference): Future[Boolean] =
    repository.add(UploadDetails(uploadId, journeyId, fileReference, InProgress)).map(_ => true)

  override def registerUploadResult(
    fileReference: Reference,
    journeyId: JourneyId,
    uploadStatus: UploadStatus
  ): Future[Boolean] =
    repository.updateStatus(fileReference, journeyId, uploadStatus).map(_ => true)

  override def getUploadResult(id: UploadId, journeyId: JourneyId): Future[Option[UploadStatus]] =
    for (result <- repository.findUploadDetails(id, journeyId)) yield result.map(_.status)

}

@ImplementedBy(classOf[MongoBackedUploadProgressTracker])
trait UploadProgressTracker {

  def requestUpload(uploadId: UploadId, journeyId: JourneyId, fileReference: Reference): Future[Boolean]

  def registerUploadResult(fileReference: Reference, journeyId: JourneyId, uploadStatus: UploadStatus): Future[Boolean]

  def getUploadResult(id: UploadId, journeyId: JourneyId): Future[Option[UploadStatus]]

}
