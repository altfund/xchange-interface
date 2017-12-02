# API to talk to knowm exchanges
    - run with ```mvn spring-boot:run```
    - experiment with invoke program provided in: https://github.com/altfund/pyxi


# Currently supported endpoints


## /getorders *encrypted method*
    - invoke getorders -e gdax 018309840441,120992038232
    - takes a list of order ids for a given exchange.
    - returns list of knowm order objects.
     - encrypted_data = {
                        exchange_credentials: "exchange_credentials",
                        order_ids: ["<id>", "<id>", ...]
                       }

## /fillorkill *encrypted method*
    - invoke fillorkill -e gdax,poloniex -o BID,BID -b ETH,ETH -q BTC,BTC -v 0.1,0.1 -p 10000,10000 -t True,True
    - takes a list of orders. Same scheme for each order as method /limitorder
    - submit N limit orders and cancels them immediately if they did no go through.

## /interexchangearbitrage *encrypted method*
    - invoke iea -e gdax,poloniex -o BID,BID -b ETH,ETH -q BTC,BTC -v 0.1,0.1 -p 10000,10000 -t True,True
    - takes a list of orders. Same scheme for each order as method /limitorder
    - submits two limit orders to try and take advantage or an arbitrage opportunity

## /availablemarkets *encrypted method*
     - invoke availablemarkets -e gdax,poloniex -c ETH/BTC/LTC,ETH/BTC/LTC
     - inputs: specify the exchanges and the corresponding currencies you can trade on those exchanges
     - returns list whose elments are a json object of a given currency pair to a list of exchanges that have that market.
     - accepts json in paramter encrypted_data:
     - encrypted_data = {
                        [{exchange: "<exchange>"
                          currencies: ["<currency>", "<currency>", ... ]
                          ...}]
       }

## /aggreagateorderbooks *encrypted method*
     - invoke aggregateorderbooks -b BTC -q ETH -e gdax,poloniex 
     - curl -X POST --data '{"base_currency": "BTC","quote_currency": "ETH", "exchanges": ["bitfinex","poloniex"]}' -H "Content-type:application/json" http://localhost:9000/aggregateorderbooks
     - List<org.knowm.xchange.dto.trade.LimitOrder>, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/LimitOrder.html 
     - Actual List is of a special type LimitOrderExchange which has the added exchange property so we know to which exchange the limit order belongs.
     - accepts json in paramter encrypted_data:
     - returns aggregate order book (asks/bids) of all the exchanges provided, for the given currency pair.
     - encrypted_data = {
                        base_currency: "<base_currency>",
                        quote_currency: "<quote_currency>",
                        exchanges: "[<exchange>,<exchange>, ... ]",
       }
```
## /openorders *encrypted method*
     - /openorders?iv=XYZ&encrypted_data=ABC
     - List<org.knowm.xchange.dto.trade.LimitOrder>, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/LimitOrder.html 
     - getsOpenOrders
        - http://knowm.org/javadocs/xchange/org/knowm/xchange/service/trade/TradeService.html#getOpenOrders-org.knowm.xchange.service.trade.params.orders.OpenOrdersParams-
     - accepts json in paramter encrypted_data:
     - encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", //String
                                   key: "<key>", //String
                                   secret="<secret>", //String
                                   passphrase="<passphrase>" // String
                               },
        open_order_params: {
                            base_currency: "<base_currency>",
                            quote_currency: "<quote_currency>",
                      } 
       }
```
## /fundinghistory *encrypted method*
     - /fundinghistory?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.trade.UserTrades, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/UserTrades.html 
     - fundinghistory
     - accepts json in paramter encrypted_data:
     - encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", //String
                                   key: "<key>", //String
                                   secret="<secret>", //String
                                   passphrase="<passphrase>" // String
                               },
        trade_params: {
                            currency_pair: "<currency_pair>",  //CurrencyPair
                            currency_pairs: "<currency_pairs>", //Collection<CurrencyPair>
                            end_id: "<end_id>", // String
                            end_time: "<end_time>", // Date
                            offset: "<offset>", // Long
                            page_length: "<page_length>",  // Integer
                            page_number: "<page_number>", //Integer
                            start_id: "<start_id>", // String
                            start_time: "<start_time>" // startTime
                      } 
       }
```
## /tradehistory *encrypted method*
     - /tradehistory?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.trade.UserTrades, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/UserTrades.html 
     - getsTradeHistory
        - http://knowm.org/javadocs/xchange/org/knowm/xchange/service/trade/TradeService.html#getTradeHistory-org.knowm.xchange.service.trade.params.TradeHistoryParams-
     - accepts json in paramter encrypted_data:
     - encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", //String
                                   key: "<key>", //String
                                   secret="<secret>", //String
                                   passphrase="<passphrase>" // String
                               },
        trade_params: {
                            currency_pair: "<currency_pair>",  //CurrencyPair
                            currency_pairs: "<currency_pairs>", //Collection<CurrencyPair>
                            end_id: "<end_id>", // String
                            end_time: "<end_time>", // Date
                            offset: "<offset>", // Long
                            page_length: "<page_length>",  // Integer
                            page_number: "<page_number>", //Integer
                            start_id: "<start_id>", // String
                            start_time: "<start_time>" // startTime
                      } 
       }
```
## /cancelorder *encrypted method*
     - invoke cancelorder -e gdax -o 2a43967e-8135-4652-b131-15f839e5db7a
     - /cancelorder?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.trade.LimitOrder, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/trade/LimitOrder.html 
     - cancels limit order
        - http://knowm.org/javadocs/xchange/org/knowm/xchange/service/trade/TradeService.html#cancelOrder-java.lang.String-
     - accepts json in paramter encrypted_data:
      encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", //String
                                   key: "<key>", //String
                                   secret="<secret>", //String
                                   passphrase="<passphrase>" // String
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
      encrypted_data = {
        exchange_credentials: {
                                   exchange: "<exchange>", //String
                                   key: "<key>", //String
                                   secret="<secret>", //String
                                   passphrase="<passphrase>" // String
                               },
        order_type: "['ASK'|'BID']", // accepts strings 'ASK' or 'BID'
        order_spec: {
                        base_currency: "<base_currency>", // Currency
                        quote_currency: "<quote_currency>", // Currency
                        volume: "<volume>", //BigDecimal
                        price: "<price>",// BigDecimal
                        test: "<test>" //boolean
                    }
        }
    ```

## /balance *encrypted method*
     - invoke balance -e gdax
     - /balance?iv=XYZ&encrypted_data=ABC
     - org.knowm.xchange.dto.account.Balance, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/account/Balance.html 
     - every balance for each wallet on given <exchange>.
     - accepts json in paramter encrypted_data:
     ```
     encrypted_data = {exchange: "<exchange>", key: "<key>", secret="<secret>", passphrase="<passphrase>"}
     ```

## /tradefees
     - /tradefees?exchange=<exchange>
    - org.knowm.xchange.dto.meta.CurrencyPairMetaData, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/meta/CurrencyPairMetaData.html
    - currency pair metadata for each currency pair on given <exchange>.

## /isfeasible
    - invoke isfeasible -e gdax
    - /isfeasible?exchange=<exchange>
    
## /exchangesymbols
    - invoke exchangesymbols -e gdax
    - /exchangesymbols?exchange=<exchange>
    - exchange symbols on given <exchange>.

## /currency
    - invoke currency -e gdax
    - /currency?exchange=<exchange>
    - org.knowm.xchange.currency.Currency, http://knowm.org/javadocs/xchange/org/knowm/xchange/currency/Currency.html
    - currencies on given <exchange>.

## /orderbook
     - /orderbook?exchange=<exchange>&base_currency=<currency>&quote_currency=<currency>
    - org.knowm.xchange.dto.marketdata.OrderBook, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/marketdata/OrderBook.html
    - the order book (asks and bids) for the given base_currency and quote_currency on the given <exchange>.

## /ticker
     - /ticker?exchange=<exchange>
    - org.knowm.xchange.dto.marketdata.Ticker, http://knowm.org/javadocs/xchange/org/knowm/xchange/dto/marketdata/Ticker.html
    - ticker for each currency pair on given <exchange>.

