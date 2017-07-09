#!/bin/bash

# samples for the /currency endpoint
curl -X GET "http://localhost:9000/currency?exchange=GDAX"
curl -X GET "http://localhost:9000/currency?exchange=FOO"
curl -X GET "http://localhost:9000/currency?exchange=gdax"
curl -X GET "http://localhost:9000/currency?exchange=org.knowm.xchange.gdax.GDAXExchange"
