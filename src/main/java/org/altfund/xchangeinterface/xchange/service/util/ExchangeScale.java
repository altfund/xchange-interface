package org.altfund.xchangeinterface.xchange.service.util;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.CurrencyMetaData;

@Slf4j
public class ExchangeScale {

    public int getExchangeScale(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {
        //Optional<org.knowm.xchange.Exchange> exchange =
        //    Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<CurrencyPairMetaData> exCurPairMetaData =
            Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
        //if (exchangeMetaData.isPresent())
        //    log.debug("exchange meta data {}.", exchangeMetaData.get());

        Optional<CurrencyMetaData> exCurMd =
            Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.base));

        Optional<Integer> exBaseCurScale =
            Optional.ofNullable(exCurMd.get().getScale());

        if (exCurPairMetaData.isPresent()) {
            log.debug("exchange cur pairs {}.", exCurPairMetaData.get());

            if (exCurPairMetaData.get() != null) {
                if (exCurPairMetaData.get().getMinimumAmount() != null) {
                    log.debug("found xchange scale in the currency pair meta data.");
                    //if (buyTradableAmount.compareTo(exCurPairMetaData.get().getMinimumAmount()) < 0)
                    //    log.error("Tradeable amount to buy is less than minimum amount supported by exchange {}", orderSpec.getSellExchange());
                     //TODO throq eroor instead of log.
                    return exCurPairMetaData.get().getMinimumAmount().scale();
                } else if (exBaseCurScale.isPresent()){
                    log.debug("inferred xchange scale from the base currency.");
                    return exBaseCurScale.get();
                } else {
                    log.debug("Randomly setting base scale to 5.");
                    return 5;
                }
            }
        }
        return 5;
    }
}
