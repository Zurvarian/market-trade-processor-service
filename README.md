# market-trade-processor-service
Small app that collects events with the format:
{"userId": "134256", "currencyFrom": "EUR", "currencyTo": "USD", "amountSell": 1000,
"amountBuy": 747.10, "rate": 0.7471, "timePlaced" : "13-NOV-22 17:27:44", "originatingCountry"
: "FR"}

It transforms the incoming data into units that represent the volume of incoming entries.

Then these volumes are pushed to the clients via Text Stream connection.

## Key items
* I've used Spring webflux to handle the load as it is non-blocking which increases the throughput. Also, I've used R2DBC which applies the same reactive goodness to the DB layer.
* I've focused the code into being simple and readable, but I'm fully aware is far from being production ready.
* I'm using Stream Event Source to communicate with the UI for two reasons: First, I've never used it before and wanted to take the opportunity and try it out, 
second, because as we don't need the UI to reply I did not see the need to add the extra hustle of using WebSockets.

## Leftovers
I couldn't do all the things I wanted to, some lefovers being:
* Configure Maven to create the Docker image of the product (Using Spring thin layers, the resulting image would only require a small thin layer for the app)
* Extract the CORS security into a module that is only loaded when a `dev` spring profile is provided, in prod we can let the Load Balancer to take care of cross-origin requests.
* Add a script to load data into the app, a small load generator.
