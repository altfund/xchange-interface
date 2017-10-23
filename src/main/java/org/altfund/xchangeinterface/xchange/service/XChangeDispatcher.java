package org.altfund.xchangeinterface.xchange.service;

public  enum  XChangeDispatcher {

    /*
     * void remoteInit()?
     */
    KnowmExchangeType, ExchangeCurrencyType, ExchangeSymbolsType, AccountServiceType, ExchangeSpecificationType, DefaultExchangeSpecificationType, MarketDataServiceType, TradeServiceType;

    @SuppressWarnings("unchecked")
    public <T> T comeback(org.knowm.xchange.Exchange exchange) {
        switch (this) {
            case KnowmExchangeType:
                return (T) exchange;
            case ExchangeCurrencyType:
                return (T) exchange.getExchangeMetaData();
            case ExchangeSymbolsType:
                return (T) exchange.getExchangeSymbols();
            case AccountServiceType:
                return (T) exchange.getAccountService();
            case ExchangeSpecificationType:
                return (T) exchange.getExchangeSpecification();
            case DefaultExchangeSpecificationType:
                return (T) exchange.getDefaultExchangeSpecification();
            case MarketDataServiceType:
                return (T) exchange.getMarketDataService();
            case TradeServiceType:
                return (T) exchange.getTradeService();
            default:
                return null;
        }
    }
}
