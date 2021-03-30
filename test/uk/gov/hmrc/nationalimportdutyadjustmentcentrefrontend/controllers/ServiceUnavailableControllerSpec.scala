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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.ServiceUnavailablePage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class ServiceUnavailableControllerSpec extends ControllerSpec {

  val page: ServiceUnavailablePage = mock[ServiceUnavailablePage]

  private val controller = new ServiceUnavailableController(stubMessagesControllerComponents(), page)

  "GET" should {
    "return OK" in {
      when(page.apply()(any(), any())).thenReturn(HtmlFormat.empty)

      val result = controller.onPageLoad(fakeGetRequest)
      status(result) mustBe Status.OK
    }

  }
}
