
# National Import Duty Adjustment Centre Frontend

## Local Setup

1. Checkout this repo
1. Start dependent services with [service-manager](https://github.com/hmrc/service-manager): `sm --start NIDAC_ALL`
1. Ensure you have a local Mongo db instance running (on the default port `27017`)   
1. Stop the `service-manager` owned version of the service: `sm --stop NATIONAL_IMPORT_DUTY_ADJUSTMENT_CENTRE_FRONTEND`
1. Start the service: `sbt run`

Open the app in your browser at `http://localhost:8490/apply-for-return-import-duty-paid-on-deposit-or-guarantee`

## Tampermonkey

[Tampermonkey](https://www.tampermonkey.net/) scripts are provided for testing / demonstration purposes.

Note:  The scripts are not a deliverable part of this project and are not guaranteed to be kept up to date with changes in the UI.

Use the following urls to install the scripts into the Tampermonkey browser plugin.

Auth Login Stub
https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC_Auth_Stub.js

Create Case Journey
https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC_Create_Claim.js

Amend Case Journey
https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC_Amend_Claim.js

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
