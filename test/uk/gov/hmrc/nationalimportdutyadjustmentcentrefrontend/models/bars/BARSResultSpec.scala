package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.bars

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}

class BARSResultSpec extends UnitSpec with TestData {

  "BARSResult" should {

    "be valid if sort code matches account and roll is not required and bacs is supported" in {

      BARSResult("yes", Some("no"), Some("yes")).isValid mustBe true
    }

    "be invalid if sort code does not match account" in {

      BARSResult("no", Some("yes"), None).isValid mustBe false
    }

    "be invalid if it is indeterminate if sort code and account match" in {

      BARSResult("indeterminate", Some("inapplicable"), None).isValid mustBe false
    }

    "be invalid if roll IS required" in {

      BARSResult("yes", Some("yes"), Some("yes")).isValid mustBe false
    }

    "be invalid if bacs is not supported" in {

      BARSResult("yes", Some("no"), Some("no")).isValid mustBe false
    }


  }

}
