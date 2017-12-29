package org.altfund.xchangeinterface.xchange.service.util;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import java.math.BigDecimal;

@Slf4j
public class ExchangeScale {

    public BigDecimal getMinimumAmount(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {
        //Optional<org.knowm.xchange.Exchange> exchange =
        //    Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<CurrencyPairMetaData> exCurPairMetaData =
            Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));

        if (exCurPairMetaData.isPresent() && exCurPairMetaData.get().getMinimumAmount() != null) {
            log.debug("found xchange scale in the currency pair meta data minimum amt.");
            log.debug("setting min amt: {}", exCurPairMetaData.get().getMinimumAmount());
            return exCurPairMetaData.get().getMinimumAmount();
        }
        return new BigDecimal("-1");
    }

    /* quote we want to use the price scale determined by the market.
     *
     */
    public int getQuoteScale(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {

        //TODO check BCH/USD metadata, if it is correct stop this nonsense.
        CurrencyPair currencyPair = new CurrencyPair("BCH","USD");
        if (cp.compareTo(currencyPair) == 0) {
            log.debug("Randomly setting base scale to 2. SUPER AWFUL BREAK IN CODE DUE TO BCH/USD METADATA ERROR");
            log.debug("setting quote scale: {}", 2);
            return 2;
        }

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<Integer> exQuoteCurScale = null;
        Optional<CurrencyMetaData> exQuoteCurMd = null;
        Optional<CurrencyPairMetaData> exCurPairMetaData = null;
        Optional<Integer> exCurPairMetaDataPriceScale = null;

        if (exchangeMetaData.isPresent()) {
            exCurPairMetaData = Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
            if (exCurPairMetaData.isPresent()) {
                exCurPairMetaDataPriceScale = Optional.ofNullable(exCurPairMetaData.get().getPriceScale());
            }
            exQuoteCurMd = Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.counter));
        }

        if (exQuoteCurMd != null && exQuoteCurMd.isPresent()) {
            exQuoteCurScale = Optional.ofNullable(exQuoteCurMd.get().getScale());
        }

        if (exCurPairMetaDataPriceScale != null && exCurPairMetaDataPriceScale.isPresent()) {
            log.debug("Currency pair meta data for given currency pair {}", exCurPairMetaData.get());
            log.debug("found xchange scale in the currency pair meta data price scale 1st preference: PREFERRED!");
            log.debug("setting quote scale: {}", exCurPairMetaDataPriceScale.get());
            return exCurPairMetaDataPriceScale.get();
        }
        else if (exCurPairMetaData != null && exCurPairMetaData.isPresent() && exCurPairMetaData.get().getMinimumAmount() != null) {
            log.debug("found xchange scale in the currency pair meta data minimum amt. 2nd prefernce");
            log.debug("setting quote scale: {}", exCurPairMetaData.get().getMinimumAmount().scale());
            return exCurPairMetaData.get().getMinimumAmount().scale();
        } else if (exQuoteCurScale !=  null && exQuoteCurScale.isPresent()){
            log.debug("inferred xchange scale from the quote currency. 3rd preference");
            log.debug("setting quote scale: {}", exQuoteCurScale.get());
            return exQuoteCurScale.get();
        }
        log.debug("Randomly setting base scale to 2. 4th preference, LEAST PREFERRED");
        log.debug("setting quote scale: {}", 2);
        return 2;
    }

    /*
    private int getScale() {
        TODO make getBaseScale and getQuoteScale same method AND figure out
        which approximation for scale is better?
    }
    */

    /*
     *
     */
    public int getBaseScale(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {
        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<Integer> exBaseCurScale = null;
        Optional<CurrencyMetaData> exBaseCurMd = null;
        /*
        Optional<CurrencyPairMetaData> exCurPairMetaData = null;
        */

        if (exchangeMetaData.isPresent()) {
            //exCurPairMetaData = Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
            exBaseCurMd = Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.base));
        }

        if (exBaseCurMd != null && exBaseCurMd.isPresent()) {
            exBaseCurScale = Optional.ofNullable(exBaseCurMd.get().getScale());
        }

        if (exBaseCurScale != null && exBaseCurScale.isPresent()){
            log.debug("inferred xchange scale from the base currency.1st preference PREFERRED");
            log.debug("setting base scale: {}", exBaseCurScale.get());
            return exBaseCurScale.get();
        }
        /*
        else if (exCurPairMetaData != null && exCurPairMetaData.isPresent() && exCurPairMetaData.get().getPriceScale() != null) {
            log.debug("found xchange scale in the currency pair meta data price scale.");
            log.debug("setting base scale: {}", exCurPairMetaData.get().getPriceScale());
            return exCurPairMetaData.get().getPriceScale();
        }
        */
        log.debug("Randomly setting base scale to 2. 2nd preference, LEAST PREFERRED");
        log.debug("setting base scale: {}", 2);
        return 2;
    }
}
