# API to talk to knowm exchanges

## run with ```mvn spring-boot:run```
## test with ```curl -s 'http://localhost:9000/ticker?exchange=gdax```

# Currently supported endpoints

## /cancelorder *encrypted method*
     - /cancelorder?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.trade.LimitOrder, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/LimitOrder.html 
     - cancels limit order
        - http://knowm.org/javadocs/xchange/org/knowm/xchange/service/trade/TradeService.html#cancelOrder-java.lang.String-
     - accepts json in paramter encrypted_data:
 ```
     - encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", 
                                   key: "<key>", 
                                   secret="<secret>", 
                                   passphrase="<passphrase>"
                               },
        order_id: "<order_id>" 
        }
```
## /limitorder *encrypted method*
     - /limitorder?iv=XYZ&encrypted_data=ABC
     - places limit order
        - http://knowm.org/javadocs/xchange/org/knowm/xchange/service/trade/TradeService.html#placeLimitOrder-org.knowm.xchange.dto.trade.LimitOrder-
     - org.knowm.xchange.dto.trade.LimitOrder, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/LimitOrder.html 
     - accepts json in paramter encrypted_data:
 ```
     - encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", 
                                   key: "<key>", 
                                   secret="<secret>", 
                                   passphrase="<passphrase>"
                               },
        order_type: "['ASK'|'BID']", // accepts strings 'ASK' or 'BID'
        order_spec: {
                        base_currency: "<base_currency>",
                        quote_currency: "<quote_currency>",
                        volume: "<volume>",
                        price: "<price>",
                        test: "<test>" 
                    }
        }
```
## /balance *encrypted method*
     - /balance?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.account.Balance, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/account/Balance.html 
     - every balance for each wallet on given <exchange>.
     - accepts json in paramter encrypted_data:
     ```
     encrypted_data = {exchange: "<exchange>", key: "<key>", secret="<secret>", passphrase="<passphrase>"}
     ```
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

