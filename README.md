# API to talk to knowm exchanges

## run with ```mvn spring-boot:run```
## test with ```curl -s 'http://localhost:9000/ticker?exchange=gdax```

# Currently supported endpoints
## /balance
 * /balance?exchange=<exchange>&key=<key>&secret=<secret>[&passphrase=<passphrase>]
    - org.knowm.xchange.dto.account.Balance, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/account/Balance.html
    - every balance for each wallet on given <exchange>.
##/tradefees
 * /tradefees?exchange=<exchange>
    - org.knowm.xchange.dto.meta.CurrencyPairMetaData, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/meta/CurrencyPairMetaData.html
    - currency pair metadata for each currency pair on given <exchange>.
##/currency
 * /currency?exchange=<exchange>
    - org.knowm.xchange.currency.Currency, http://knowm.org/javadocs/xchange/org/knowm/xchange/currency/Currency.html
    - currencies on given <exchange>.
##/orderbook
 * /orderbook?exchange=<exchange>&base_currency=<currency>&quote_currency=<currency>
    - org.knowm.xchange.dto.marketdata.OrderBook, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/marketdata/OrderBook.html
    - the order book (asks and bids) for the given base_currency and quote_currency on the given <exchange>.
##ticker
 * /ticker?exchange=<exchange>
    - org.knowm.xchange.dto.marketdata.Ticker, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/marketdata/Ticker.html
    - ticker for each currency pair on given <exchange>.

