package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Implicits.SanitizedString

case class AddressLookupConfirmation(auditRef: String, id: String, addressLine2: Option[String], city: String, postCode: String)

object AddressLookupConfirmation {
  implicit val format: OFormat[AddressLookupConfirmation] = Json.format[AddressLookupConfirmation]

  def apply(auditRef: String, id: String, address: Option[String], city: String, postCode: String): AddressLookupConfirmation =
    new AddressLookupConfirmation(name, addressLine1, addressLine2, city, postCode.stripExternalAndReduceInternalSpaces())

}
}
/*
/*
{
    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
    "id" : "GB990091234524",
    "address" : {
        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
        "postcode" : "ZZ1 1ZZ",
        "country" : {
            "code" : "GB",
            "name" : "United Kingdom"
        }
    }
}
 */
 */