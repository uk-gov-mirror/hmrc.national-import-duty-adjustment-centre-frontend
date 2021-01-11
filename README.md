
# National Import Duty Adjustment Centre Frontend

## Local Setup

1. Checkout this repo
1. Start dependent services with [service-manager](https://github.com/hmrc/service-manager): `sm --start NIDAC_ALL`
1. Ensure you have a local Mongo db instance running (on the default port `27017`)   
1. Stop the `service-manager` owned version of the service: `sm --stop NATIONAL_IMPORT_DUTY_ADJUSTMENT_CENTRE_FRONTEND`
1. Start the service: `sbt run`

Open the app in your browser at `http://localhost:8490/national-import-duty-adjustment-centre`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
