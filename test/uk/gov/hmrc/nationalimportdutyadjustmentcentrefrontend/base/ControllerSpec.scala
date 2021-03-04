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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.FakeIdentifierActions
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CacheData
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.{AmendNavigator, CreateNavigator}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.FakeRequestCSRFSupport.CSRFFakeRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

import scala.concurrent.{ExecutionContext, Future}

trait ControllerSpec
    extends UnitSpec with MockitoSugar with Injector with FakeIdentifierActions with BeforeAndAfterEach {

  implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val dataRepository: CacheDataRepository = mock[CacheDataRepository]

  val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  val cacheDataService: CacheDataService = new CacheDataService(dataRepository)

  val navigator      = instanceOf[CreateNavigator]
  val amendNavigator = instanceOf[AmendNavigator]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(dataRepository.set(any[CacheData])).thenReturn(Future.successful(None))
  }

  override protected def afterEach(): Unit = {
    reset(dataRepository)
    super.afterEach()
  }

  def withEmptyCache(): Unit = when(dataRepository.get(anyString())).thenReturn(Future.successful(None))

  def withCacheCreateAnswers(answers: CreateAnswers): Unit = {
    val cacheData: Option[CacheData] = Some(CacheData("id", createAnswers = Some(answers)))
    when(dataRepository.get(anyString())).thenReturn(Future.successful(cacheData))
  }

  def withCacheAmendAnswers(answers: AmendAnswers): Unit = {
    val cacheData: Option[CacheData] = Some(CacheData("id", amendAnswers = Some(answers)))
    when(dataRepository.get(anyString())).thenReturn(Future.successful(cacheData))
  }

  def withCachedClaimResponse(createClaimResponse: Option[CreateClaimResponse]): Unit = {
    val cacheData: Option[CacheData] = Some(CacheData("id", createClaimResponse = createClaimResponse))
    when(dataRepository.get(anyString())).thenReturn(Future.successful(cacheData))
  }

  def withCachedAmendClaimResponse(amendClaimResponse: Option[AmendClaimResponse]): Unit = {
    val cacheData: Option[CacheData] = Some(CacheData("id", amendClaimResponse = amendClaimResponse))
    when(dataRepository.get(anyString())).thenReturn(Future.successful(cacheData))
  }

  protected def theUpdatedCreateAnswers: CreateAnswers =
    theUpdatedCacheDate.getCreateAnswers

  protected def theUpdatedAmendAnswers: AmendAnswers =
    theUpdatedCacheDate.getAmendAnswers

  private def theUpdatedCacheDate: CacheData = {
    val captor = ArgumentCaptor.forClass(classOf[CacheData])
    verify(dataRepository).set(captor.capture())
    captor.getValue
  }

  protected def postRequest(data: (String, String)*): Request[AnyContentAsFormUrlEncoded] =
    FakeRequest("POST", "")
      .withFormUrlEncodedBody(data: _*)
      .withCSRFToken

}
