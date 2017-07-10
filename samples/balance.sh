#!/bin/bash

# samples for the /balance endpoint
curl -X GET "http://localhost:9000/balance?exchange=bitfinex&key=YYY&secret=XXX"
curl -X GET "http://localhost:9000/balance?exchange=gdax&key=YYY&secret=XXX&passphrase=ZZZ"
curl -X GET "http://localhost:9000/balance?exchange=kraken&key=YYY&secret=XXX"
curl -X GET "http://localhost:9000/balance?exchange=poloniex&key=YYY&secret=XXX"
